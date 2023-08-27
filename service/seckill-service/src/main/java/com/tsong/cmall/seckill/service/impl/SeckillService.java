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
import com.tsong.cmall.seckill.enums.SeckillStatusEnum;
import com.tsong.cmall.seckill.mapper.SeckillMapper;
import com.tsong.cmall.seckill.mapper.SeckillSuccessMapper;
import com.tsong.cmall.seckill.redis.RedisCache;
import com.tsong.cmall.seckill.service.ISeckillService;
import com.tsong.cmall.seckill.web.vo.SeckillGoodsVO;
import com.tsong.cmall.seckill.web.vo.SeckillSuccessVO;
import com.tsong.cmall.seckill.web.vo.UrlExposerVO;
import com.tsong.feign.clients.goods.GoodsClient;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.ORDER_SECKILL_CREATE;
import static com.tsong.cmall.common.enums.ServiceResultEnum.RPC_ERROR;


/**
 * @Author Tsong
 * @Date 2023/3/25 13:42
 */
@Service
public class SeckillService implements ISeckillService {

    // 使用令牌桶RateLimiter 限流
    private static final RateLimiter rateLimiter = RateLimiter.create(100);

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SeckillSuccessMapper seckillSuccessMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MessageHandler messageHandler;


    @Override
    public boolean hasStock(Long seckillId) {
        Integer stock = redisCache.getCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        return stock != null && stock > 0;
    }

    @Override
    public UrlExposerVO exposeUrl(Long seckillId) {
        SeckillGoodsVO seckillGoodsVO = redisCache.getCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        Date startTime = seckillGoodsVO.getSeckillBegin();
        Date endTime = seckillGoodsVO.getSeckillEnd();
        // 系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new UrlExposerVO(SeckillStatusEnum.NOT_START, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 检查虚拟库存
        Integer stock = redisCache.getCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        if (stock == null || stock <= 0) {
            return new UrlExposerVO(SeckillStatusEnum.STARTED_SHORTAGE_STOCK, seckillId);
        }
        // 加密
        String md5 = MD5Util.MD5Encode(seckillId.toString(), Constants.UTF_ENCODING);
        return new UrlExposerVO(SeckillStatusEnum.START, md5, seckillId);
    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public SeckillSuccessVO executeSeckill(Long seckillId, Long userId, Long addressId) {
        // 判断能否在100毫秒内得到令牌，如果不能则立即返回false，不会阻塞程序
        if (!rateLimiter.tryAcquire(100, TimeUnit.MILLISECONDS)) {
            CMallException.fail("秒杀失败");
        }
        // 判断用户是否购买过秒杀商品
        if (redisCache.containsCacheSet(Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId)) {
            CMallException.fail("您已经购买过秒杀商品，请勿重复购买");
        }

        // 更新秒杀商品虚拟库存，登记userId
        // 返回剩余库存情况
        // 事务
        Long stock = redisCache.luaDecrement(Constants.SECKILL_GOODS_STOCK_KEY + seckillId,
                Constants.SECKILL_SUCCESS_USER_ID + seckillId,
                userId);
        if (stock < 0) {
            CMallException.fail("秒杀商品已售空");
        }

        // 判断秒杀商品是否在有效期内
        long nowTime = System.currentTimeMillis();
        // 从redis中获得秒杀事件
        Seckill seckill = redisCache.getCacheObject(Constants.SECKILL_KEY + seckillId);
        // 如果redis中没有，则从mysql中获取，存入redis中
        if (seckill == null) {
            seckill = seckillMapper.selectByPrimaryKey(seckillId);
            redisCache.setCacheObject(
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

        Map<String, Object> map = new HashMap<>(8);
        map.put("seckillId", seckillId);
        map.put("userId", userId);
        map.put("killTime", new Date());
        map.put("result", null);
        // 执行存储过程，result被赋值
        try {
            seckillMapper.killByProcedure(map); // 减库存操作
        } catch (Exception e) {
            e.printStackTrace();
            CMallException.fail("服务器异常");
        }
        // 获取result -2sql执行失败 -1未插入数据 0未更新数据 1sql执行成功
        // map.get("result");
        int result = MapUtils.getInteger(map, "result", -2);
        if (result != 1) {
            CMallException.fail("很遗憾！未抢购到秒杀商品");
        }
        // result == 1 说明秒杀成功，并且秒杀成功表插入了一条该用户秒杀成功的数据

        // 获得该用户的秒杀成功
        SeckillSuccess seckillSuccess = seckillSuccessMapper
                .getSeckillSuccessByUserIdAndSeckillId(userId, seckillId);
        // 传回前端结果
        SeckillSuccessVO seckillSuccessVO = new SeckillSuccessVO();
        Long seckillSuccessId = seckillSuccess.getSecId();
        seckillSuccessVO.setSeckillSuccessId(seckillSuccessId);
        seckillSuccessVO.setMd5(
                MD5Util.MD5Encode(
                        seckillSuccessId + Constants.SECKILL_ORDER_SALT, Constants.UTF_ENCODING));

        // 消息队列
        // 创建订单
        CreateSeckillOrderMsg msg = new CreateSeckillOrderMsg();
        msg.setUserId(userId);
        msg.setSeckillId(seckillId);
        msg.setAddressId(addressId);
        msg.setGoodsId(seckill.getGoodsId());
        msg.setSeckillPrice(seckill.getSeckillPrice());
        messageHandler.sendMessage(CMALL_DIRECT, ORDER_SECKILL_CREATE, msg);
        // 返回秒杀成功的凭证，让用户轮询查订单
        return seckillSuccessVO;
    }

    @Override
    public SeckillGoodsVO getSeckillGoodsDetail(Long seckillId) {
        SeckillGoodsVO seckillGoodsVO = redisCache.getCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        if (seckillGoodsVO != null) {
            return seckillGoodsVO;
        }

        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        if (!seckill.getSeckillStatus()){
            return null;
        }

        seckillGoodsVO = new SeckillGoodsVO();
        BeanUtil.copyProperties(seckill, seckillGoodsVO);

        // 秒杀的商品
        Result goodsResult = goodsClient.getGoodsById(seckill.getGoodsId());
        if (goodsResult.getResultCode() != 200){
            CMallException.fail(RPC_ERROR.getResult() + goodsResult.getMessage());
        }
        GoodsInfo goodsInfo = (GoodsInfo) goodsResult.getData();

        BeanUtil.copyProperties(goodsInfo, seckillGoodsVO);
//        seckillGoodsVO.setGoodsName(goodsInfo.getGoodsName());
//        seckillGoodsVO.setGoodsIntro(goodsInfo.getGoodsIntro());
//        seckillGoodsVO.setGoodsDetailContent(goodsInfo.getGoodsDetailContent());
//        seckillGoodsVO.setGoodsCoverImg(goodsInfo.getGoodsCoverImg());
        seckillGoodsVO.setGoodsCarousel(goodsInfo.getGoodsCarousel().split(","));
//        seckillGoodsVO.setSellingPrice(goodsInfo.getSellingPrice());
//        seckillGoodsVO.setSeckillBegin(seckillGoodsVO.getSeckillBegin());
//        seckillGoodsVO.setSeckillEnd(seckillGoodsVO.getSeckillEnd());

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
            Result goodsResult = goodsClient.getGoodsById(seckill.getGoodsId());
            if (goodsResult.getResultCode() != 200){
                CMallException.fail(RPC_ERROR.getResult() + goodsResult.getMessage());
            }
            GoodsInfo goodsInfo = (GoodsInfo) goodsResult.getData();
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
            if (seckillMapper.putOffBatch(seckillIds) <= 0){
                CMallException.fail("无法设置秒杀过期下架");
            }
            deleteSeckillFromCache(seckillIds);
        }

        // 放入redis
        redisCache.setCacheObject(Constants.SECKILL_GOODS_LIST, seckillGoodsVOList, 60 * 60 * 2, TimeUnit.SECONDS);
        return seckillGoodsVOList;
    }

    @Override
    public void stockRecover(Long userId, Long seckillId) {
        SeckillSuccess seckillSuccess = seckillSuccessMapper
                .getSeckillSuccessByUserIdAndSeckillId(userId, seckillId);
        // 清除秒杀成功记录
        if (seckillSuccessMapper.deleteByPrimaryKey(seckillSuccess.getSecId()) <= 0) {
            CMallException.fail("清除秒杀记录失败");
        }
        // 恢复实际库存
        if (!seckillMapper.addStock(seckillId)) {
            CMallException.fail("恢复库存失败");
        }
        // 恢复虚拟库存
        redisCache.increment(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        // 清除缓存中的用户秒杀记录
        redisCache.deleteCacheSetMember(Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId);
    }

    private void deleteSeckillFromCache(List<Long> seckillIds){
        for (Long seckillId : seckillIds) {
            redisCache.deleteObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        }
        redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
    }
}
