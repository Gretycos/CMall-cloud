package com.tsong.cmall.seckill.service;

import com.tsong.cmall.entity.Seckill;
import com.tsong.cmall.seckill.web.vo.SeckillGoodsVO;
import com.tsong.cmall.seckill.web.vo.SeckillSuccessVO;
import com.tsong.cmall.seckill.web.vo.UrlExposerVO;

import java.util.List;

public interface ISeckillService {
    boolean hasStock(Long seckillId);

    UrlExposerVO exposeUrl(Long seckillId);

    SeckillSuccessVO executeSeckill(Long seckillId, Long userId, Long addressId);

    SeckillGoodsVO getSeckillGoodsDetail(Long seckillId);

    List<SeckillGoodsVO> getSeckillGoodsList();

    void stockRecover(Long userId, Long seckillId);
}
