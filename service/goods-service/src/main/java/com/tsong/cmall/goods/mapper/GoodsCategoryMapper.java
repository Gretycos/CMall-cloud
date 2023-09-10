package com.tsong.cmall.goods.mapper;

import com.tsong.cmall.entity.GoodsCategory;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface GoodsCategoryMapper {
    List<GoodsCategory> selectByPrimaryKeys(List<Long> ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel, int number);
}