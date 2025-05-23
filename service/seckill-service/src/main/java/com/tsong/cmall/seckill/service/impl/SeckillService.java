package com.tsong.cmall.seckill.service.impl;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.mq.MessageHandler;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.MD5Util;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.entity.Seckill;
import com.tsong.cmall.entity.SeckillSuccess;
import com.tsong.cmall.msg.CreateSeckillOrderMsg;
import com.tsong.cmall.seckill.bloomfilter.BFilter;
import com.tsong.cmall.seckill.enums.SeckillStatusEnum;
import com.tsong.cmall.seckill.mapper.SeckillMapper;
import com.tsong.cmall.seckill.mapper.SeckillSuccessMapper;
import com.tsong.cmall.seckill.mapper.dto.SeckillSuccessDTO;
import com.tsong.cmall.seckill.redis.RedisCache;
import com.tsong.cmall.seckill.service.ISeckillService;
import com.tsong.cmall.seckill.web.vo.SeckillGoodsVO;
import com.tsong.cmall.seckill.web.vo.SeckillSuccessVO;
import com.tsong.cmall.seckill.web.vo.UrlExposerVO;
import com.tsong.feign.clients.goods.GoodsClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.*;
import static com.tsong.cmall.common.enums.ServiceResultEnum.RPC_ERROR;
import static com.tsong.cmall.seckill.enums.SeckillConfigEnum.SECKILL_STOCK_RECOVER_OVERTIME_MILLISECOND;


/**
 * @Author Tsong
 * @Date 2023/3/25 13:42
 */
@Service
@Slf4j
public class SeckillService implements ISeckillService {
    // 使用令牌桶RateLimiter 限流
    // 初始化每秒能够通过的请求数
//    private static final RateLimiter rateLimiter = RateLimiter.create(500);

//    private static final Logger logger = LoggerFactory.getLogger(SeckillService.class);

    @Resource
    private SeckillMapper seckillMapper;

    @Resource
    private SeckillSuccessMapper seckillSuccessMapper;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private RedisCache redisCache;

    @Resource
    private MessageHandler messageHandler;

    @Override
    public boolean hasStock(Long seckillId) {
        Integer stock = redisCache.getCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        return stock != null && stock > 0;
    }

    @Override
    @BFilter
    public UrlExposerVO exposeUrl(Long seckillId) {
        // 先找redis
        // 最好预热的时候redis中有，否则所有线程涌入后方
        UrlExposerVO urlExposerVO = redisCache.getCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId);
        if (urlExposerVO != null) {
            return urlExposerVO;
        }

        return createUrlExposerVO(seckillId);
    }

    private UrlExposerVO createUrlExposerVO(Long seckillId) {
        UrlExposerVO urlExposerVO;
        SeckillGoodsVO seckillGoodsVO = redisCache.getCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        if (seckillGoodsVO == null) {
            seckillGoodsVO = getSeckillGoodsDetail(seckillId);
        }

        Date startTime = seckillGoodsVO.getSeckillBegin();
        Date endTime = seckillGoodsVO.getSeckillEnd();
        // 系统当前时间
        Date nowTime = new Date();
        // 未开始
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            urlExposerVO = new UrlExposerVO(SeckillStatusEnum.NOT_START, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
            if (nowTime.getTime() < startTime.getTime()){
                redisCache.setCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId, urlExposerVO,
                        startTime.getTime() - nowTime.getTime(), TimeUnit.MILLISECONDS);
            } else {
                redisCache.setCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId, urlExposerVO);
            }
            return urlExposerVO;
        }
        // 检查虚拟库存
        Integer stock = redisCache.getCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        if (stock == null || stock <= 0) {
            // 库存不足
            urlExposerVO = new UrlExposerVO(SeckillStatusEnum.STARTED_SHORTAGE_STOCK, seckillId);
            // 空库存状态
            redisCache.setCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId, urlExposerVO,
                    (long) SECKILL_STOCK_RECOVER_OVERTIME_MILLISECOND.getTime(), TimeUnit.MILLISECONDS);
            return urlExposerVO;
        }
        // 加密
        String md5 = MD5Util.MD5Encode(seckillId.toString(), Constants.UTF_ENCODING);
        urlExposerVO = new UrlExposerVO(SeckillStatusEnum.START, md5, seckillId);
        // 秒杀事件加密
        redisCache.setCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId, urlExposerVO,
                endTime.getTime() - nowTime.getTime(), TimeUnit.MILLISECONDS);
        return urlExposerVO;
    }

    @Override
    @BFilter
    public SeckillSuccessVO executeSeckill(Long seckillId, Long userId, Long addressId) {
//        // 判断能否在6毫秒内得到令牌，如果不能则立即返回false，不会阻塞程序
//        if (!rateLimiter.tryAcquire(6, TimeUnit.MILLISECONDS)) {
//            CMallException.fail("当前活动太火爆啦");
//        }
//        // 判断用户是否购买过秒杀商品
//        if (redisCache.containsCacheSet(Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId)) {
//            CMallException.fail("您已经购买过秒杀商品，请勿重复购买");
//        }

        // 更新秒杀商品虚拟库存，登记userId
        // 返回剩余库存情况
        // 事务
        Long stock = redisCache.luaDecrement(Constants.SECKILL_GOODS_STOCK_KEY + seckillId,
                Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId);
        if (stock < 0) {
            if (stock == -2) {
                CMallException.fail("您已经参与过秒杀活动，请勿重复参与");
            }else{
                UrlExposerVO urlExposerVO = new UrlExposerVO(SeckillStatusEnum.STARTED_SHORTAGE_STOCK, seckillId);
                // 修改加密urlVO为售罄
                redisCache.setCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId, urlExposerVO);
                CMallException.fail("秒杀商品已售空");
            }
        }

        // 判断秒杀商品是否在有效期内
        long nowTime = System.currentTimeMillis();
        // 从redis中获得秒杀事件
        Seckill seckill = redisCache.getCacheObject(Constants.SECKILL_KEY + seckillId);
        // 如果redis中没有，则从mysql中获取，存入redis中
        if (seckill == null) {
            seckill = seckillMapper.selectByPrimaryKey(seckillId);
            redisCache.setNXCacheObject(
                    Constants.SECKILL_KEY + seckillId,
                    seckill,
                    seckill.getSeckillEnd().getTime() - nowTime,
                    TimeUnit.MILLISECONDS);
        }
        long beginTime = seckill.getSeckillBegin().getTime();
        long endTime = seckill.getSeckillEnd().getTime();
        if (nowTime < beginTime) {
            CMallException.fail("秒杀未开启");
        } else if (nowTime > endTime) {
            CMallException.fail("秒杀已结束");
        }

        // 记录秒杀成功与库存减扣
        SeckillSuccessDTO seckillSuccessDTO = new SeckillSuccessDTO();
        seckillSuccessDTO.setSeckillId(seckillId);
        seckillSuccessDTO.setUserId(userId);
        seckillSuccessDTO.setCreateTime(new Date());

        // 减真实库存消息
        messageHandler.sendMessage(CMALL_DIRECT, SECKILL_STOCK_DECREASE, seckillId);
        // 记录秒杀成功消息
        messageHandler.sendMessage(CMALL_DIRECT, SECKILL_SUCCESS, seckillSuccessDTO);

        // 传回前端结果
        SeckillSuccessVO seckillSuccessVO = new SeckillSuccessVO();
        seckillSuccessVO.setMd5(
                MD5Util.MD5Encode(
                        seckillId + userId + Constants.SECKILL_ORDER_SALT, Constants.UTF_ENCODING));

        // 消息队列
        // 创建订单
        CreateSeckillOrderMsg msg = new CreateSeckillOrderMsg();
        msg.setUserId(userId);
        msg.setSeckillId(seckillId);
        msg.setAddressId(addressId);
        msg.setGoodsId(seckill.getGoodsId());
        msg.setSeckillPrice(seckill.getSeckillPrice());
        // 创建订单消息
        messageHandler.sendMessage(CMALL_DIRECT, ORDER_SECKILL_CREATE, msg);
        // 返回秒杀成功的凭证，让用户轮询查订单
        return seckillSuccessVO;
    }

    @Override
    @BFilter
    public SeckillGoodsVO getSeckillGoodsDetail(Long seckillId) {
        SeckillGoodsVO seckillGoodsVO = redisCache.getCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        if (seckillGoodsVO != null) {
            return seckillGoodsVO;
        }

        // 如果不用布隆过滤器，这里的seckillId是非法的话会让数据库崩溃
        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        if (!seckill.getSeckillStatus()){
            return null;
        }

        seckillGoodsVO = new SeckillGoodsVO();
        BeanUtil.copyProperties(seckill, seckillGoodsVO);

        // 秒杀的商品
        Result<GoodsInfo> goodsResult = goodsClient.getGoodsById(seckill.getGoodsId());
        if (goodsResult.getResultCode() != 200){
            CMallException.fail(RPC_ERROR.getResult() + goodsResult.getMessage());
        }
        GoodsInfo goodsInfo = goodsResult.getData();

        BeanUtil.copyProperties(goodsInfo, seckillGoodsVO);
        seckillGoodsVO.setGoodsCarousel(goodsInfo.getGoodsCarousel().split(","));

        // 放入redis
        redisCache.setCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId, seckillGoodsVO);
        return seckillGoodsVO;
    }

    @Override
    public List<SeckillGoodsVO> getSeckillGoodsList() {
        List<SeckillGoodsVO> seckillGoodsVOList = redisCache.getCacheObject(Constants.SECKILL_GOODS_LIST);
        if (seckillGoodsVOList != null) {
            return seckillGoodsVOList;
        }

        List<Seckill> seckillList = seckillMapper.getHomePageSeckillList();
        Date now = new Date();
        // 秒杀事件过期列表
        List<Seckill> expiredSeckillList = new ArrayList<>();
        seckillGoodsVOList = seckillList.stream().map(seckill -> {
            SeckillGoodsVO seckillGoodsVO = new SeckillGoodsVO();
            BeanUtil.copyProperties(seckill, seckillGoodsVO);
            if (seckill.getSeckillEnd().getTime() < now.getTime()){
                expiredSeckillList.add(seckill);
                return null;
            }
            // 查找商品
            Result<GoodsInfo> goodsResult = goodsClient.getGoodsById(seckill.getGoodsId());
            if (goodsResult.getResultCode() != 200){
                CMallException.fail(RPC_ERROR.getResult() + goodsResult.getMessage());
            }
            GoodsInfo goodsInfo = goodsResult.getData();
            if (goodsInfo == null) {
                return null;
            }
            seckillGoodsVO.setGoodsName(goodsInfo.getGoodsName());
            seckillGoodsVO.setGoodsCoverImg(goodsInfo.getGoodsCoverImg());
            seckillGoodsVO.setSellingPrice(goodsInfo.getSellingPrice());
            return seckillGoodsVO;
        }).filter(Objects::nonNull).toList();
        // 每次查询时会自动过滤已经过期的秒杀
        // 并且把过期的秒杀设置为下架状态
        if (!expiredSeckillList.isEmpty()){
            List<Long> seckillIds = expiredSeckillList.stream().map(Seckill::getSeckillId).toList();
            messageHandler.sendMessage(CMALL_DIRECT, SECKILL_EXPIRE, seckillIds);
        }

        // 放入redis
        redisCache.setCacheObject(Constants.SECKILL_GOODS_LIST, seckillGoodsVOList, 60 * 60 * 2, TimeUnit.SECONDS);
        return seckillGoodsVOList;
    }

    @Override
    public void stockDecrease(Long seckillId) {
        if(!seckillMapper.decreaseStock(seckillId)){
            CMallException.fail("减少库存失败");
        }
    }

    @Override
    public void stockRecover(Long userId, Long seckillId) {
        SeckillSuccess seckillSuccess = seckillSuccessMapper
                .getSeckillSuccessByUserIdAndSeckillId(userId, seckillId);
        // 清除秒杀成功记录
        if (seckillSuccess != null){
            if (seckillSuccessMapper.deleteByPrimaryKey(seckillSuccess.getSecId()) <= 0) {
                CMallException.fail("清除秒杀记录失败");
            }
        }

        // 恢复实际库存
        if (!seckillMapper.addStock(seckillId)) {
            CMallException.fail("恢复库存失败");
        }
        // 恢复虚拟库存
        redisCache.increment(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        // 清除缓存中的用户秒杀记录
        redisCache.deleteCacheSetMember(Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId);
        // 恢复秒杀URL的库存状态
        UrlExposerVO urlExposerVO = redisCache.getCacheObject(Constants.SECKILL_SECRET_URL_KEY + seckillId);

        if (urlExposerVO == null || urlExposerVO.getSeckillStatusEnum() == SeckillStatusEnum.STARTED_SHORTAGE_STOCK){
            createUrlExposerVO(seckillId);
        }

    }

    @Override
    public void expireByIds(List<Long> seckillIds) {
        if (seckillMapper.putOffBatch(seckillIds) <= 0){
            CMallException.fail("无法设置秒杀过期下架");
        }
        deleteSeckillFromCache(seckillIds);
    }

    @Override
    public void seckillSuccess(SeckillSuccessDTO seckillSuccessDTO) {
        if (seckillSuccessMapper.insertSuccessRecord(seckillSuccessDTO) <= 0){
            CMallException.fail("秒杀成功记录失败");
        }
    }

    private void deleteSeckillFromCache(List<Long> seckillIds){
        for (Long seckillId : seckillIds) {
            redisCache.deleteObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        }
        redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
    }
}
