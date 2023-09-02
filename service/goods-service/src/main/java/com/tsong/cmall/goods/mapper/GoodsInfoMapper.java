package com.tsong.cmall.goods.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.entity.GoodsInfo;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface GoodsInfoMapper {
    GoodsInfo selectByPrimaryKey(Long goodsId);

    List<GoodsInfo> selectByPrimaryKeys(List<Long> goodsIds);

    List<GoodsInfo> selectAll();

    int recoverStockNum(List<StockNumDTO> stockNumDTOS);

}