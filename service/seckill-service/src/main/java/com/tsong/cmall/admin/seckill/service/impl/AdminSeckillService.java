package com.tsong.cmall.admin.seckill.service.impl;

import com.tsong.cmall.admin.seckill.mapper.AdminSeckillMapper;
import com.tsong.cmall.admin.seckill.service.IAdminSeckillService;

import com.tsong.cmall.admin.seckill.web.params.SeckillAddParam;
import com.tsong.cmall.admin.seckill.web.params.SeckillEditParam;
import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.entity.Seckill;

import com.tsong.cmall.seckill.redis.RedisCache;
import com.tsong.cmall.seckill.web.vo.SeckillVO;
import com.tsong.feign.clients.goods.GoodsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tsong.cmall.common.enums.ServiceResultEnum.*;

/**
 * @Author Tsong
 * @Date 2023/3/25 13:42
 */
@Service
public class AdminSeckillService implements IAdminSeckillService {
    @Autowired
    private AdminSeckillMapper adminSeckillMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private RedisCache redisCache;

    @Override
    public PageResult getSeckillPage(PageQueryUtil pageUtil) {
        List<Seckill> seckillList = adminSeckillMapper.selectSeckillList(pageUtil);
        int total = adminSeckillMapper.getTotalSeckills(pageUtil);
        // 更新过期
        List<SeckillVO> expiredSeckillList = new ArrayList<>();
        // 返回结果
        List<SeckillVO> seckillVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(seckillList)){
            Date now = new Date();
            // 映射商品id列表
            List<Long> goodsIdList = seckillList.stream().map(Seckill::getGoodsId).toList();
            // 查询商品列表
            Result<List<GoodsInfo>> goodsListRes = goodsClient.getGoodsListByIds(goodsIdList);
            if (goodsListRes.getResultCode() != 200) {
                CMallException.fail(RPC_ERROR.getResult());
            }
            List<GoodsInfo> goodsInfoList = goodsListRes.getData();
            // 映射成map {goodsId: GoodsInfo}
            Map<Long, GoodsInfo> goodsInfoMap = goodsInfoList.stream().collect(
                    Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (e1, e2) -> e1));

            seckillVOList = BeanUtil.copyList(seckillList, SeckillVO.class);
            for (SeckillVO seckillVO : seckillVOList) {
                if (seckillVO.getSeckillEnd().getTime() < now.getTime()){
                    if (seckillVO.getSeckillStatus()){
                        seckillVO.setSeckillStatus(false);
                        expiredSeckillList.add(seckillVO);
                    }
                }
                GoodsInfo goodsInfo = goodsInfoMap.get(seckillVO.getGoodsId());
                if (goodsInfo == null){
                    CMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
                }
                seckillVO.setGoodsName(goodsInfo.getGoodsName());
                seckillVO.setGoodsCoverImg(goodsInfo.getGoodsCoverImg());
            }
            if (!expiredSeckillList.isEmpty()){
                List<Long> seckillIds = expiredSeckillList.stream().map(SeckillVO::getSeckillId).toList();
                if (adminSeckillMapper.putOffBatch(seckillIds) <= 0){
                    CMallException.fail("无法设置秒杀过期下架");
                }
                deleteSeckillFromCache(seckillIds);
            }
        }
        return new PageResult(seckillVOList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public SeckillVO getSeckillVOById(Long id) {
        Seckill seckill = adminSeckillMapper.selectByPrimaryKey(id);
        SeckillVO seckillVO = new SeckillVO();
        BeanUtil.copyProperties(seckill, seckillVO);
        GoodsInfo goodsInfo = getGoodsInfoRPC(seckill);
        if (goodsInfo == null){
            CMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        seckillVO.setGoodsName(goodsInfo.getGoodsName());
        seckillVO.setGoodsCoverImg(goodsInfo.getGoodsCoverImg());
        return seckillVO;
    }

    @Override
    public boolean saveSeckill(SeckillAddParam seckillAddParam) {
        Seckill seckill = new Seckill();
        BeanUtil.copyProperties(seckillAddParam, seckill);
        seckill.setCreateTime(new Date());
        GoodsInfo goodsInfo = getGoodsInfoRPC(seckill);
        if (goodsInfo == null) {
            CMallException.fail(GOODS_NOT_EXIST.getResult());
        }
        boolean res = adminSeckillMapper.insertSelective(seckill) > 0;
        if (res) {
            // 虚拟库存预热
            // mapper文件中使用了useGeneratedKeys="true" keyProperty="seckillId"
            // 所以插入成功后会返回seckillId到对象上
            redisCache.setCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckill.getSeckillId(), seckill.getSeckillNum());
        }
        return res;
    }

    private GoodsInfo getGoodsInfoRPC(Seckill seckill) {
        Result<GoodsInfo> goodsRes = goodsClient.getGoodsById(seckill.getGoodsId());
        if (goodsRes.getResultCode() != 200) {
            CMallException.fail(RPC_ERROR.getResult());
        }
        GoodsInfo goodsInfo = goodsRes.getData();
        return goodsInfo;
    }

    @Override
    public boolean updateSeckill(SeckillEditParam seckillEditParam) {
        Seckill seckill = new Seckill();
        BeanUtil.copyProperties(seckillEditParam, seckill);
        seckill.setUpdateTime(new Date());
        Seckill temp = adminSeckillMapper.selectByPrimaryKey(seckill.getSeckillId());
        if (temp == null) {
            CMallException.fail(DATA_NOT_EXIST.getResult());
        }
        // 更新时间
        seckill.setUpdateTime(new Date());
        boolean res = adminSeckillMapper.updateByPrimaryKeySelective(seckill) > 0;
        if (res) {
            // 虚拟库存预热
            redisCache.setCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckill.getSeckillId(), seckill.getSeckillNum());
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + seckill.getSeckillId());
            redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
        }
        return res;
    }

    @Override
    public boolean deleteSeckillById(Long id) {
        boolean res = adminSeckillMapper.deleteByPrimaryKey(id) > 0;
        if (res) {
            // 从缓存中去除
            redisCache.deleteObject(Constants.SECKILL_GOODS_STOCK_KEY + id);
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + id);
            redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
        }
        return res;
    }


    private void deleteSeckillFromCache(List<Long> seckillIds){
        for (Long seckillId : seckillIds) {
            redisCache.deleteObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        }
        redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
    }
}
