package com.tsong.cmall.admin.goods.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.GoodsCategory;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminGoodsCategoryMapper {
    int insertSelective(GoodsCategory row);

    GoodsCategory selectByPrimaryKey(Long categoryId);

    int updateByPrimaryKeySelective(GoodsCategory row);

    GoodsCategory selectByLevelAndName(Byte categoryLevel, String categoryName);

    List<GoodsCategory> selectGoodsCategoryList(PageQueryUtil pageUtil);

    int getTotalGoodsCategories(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel, int number);

    List<GoodsCategory> selectByLevel(int categoryLevel);
}