package com.tsong.cmall.admin.goods.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.GoodsInfo;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/9/12 00:08
 */
public interface AdminGoodsMapper {
    List<GoodsInfo> selectGoodsList(PageQueryUtil pageUtil);

    int getTotalGoods(PageQueryUtil pageUtil);

    int insertSelective(GoodsInfo row);

    GoodsInfo selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(GoodsInfo row);

    GoodsInfo selectByCategoryIdAndName(String goodsName, Long goodsCategoryId);

    GoodsInfo selectByIdAndCreateUser(Long goodsId, Long createUser);

    List<GoodsInfo> selectByCreateUser(Long createUser);

    int batchUpdateSaleStatus(Long[] goodsIds, int saleStatus, Long createUser);

    List<GoodsInfo> selectByPrimaryKeys(List<Long> goodsIds);

    List<GoodsInfo> selectAll();
}
