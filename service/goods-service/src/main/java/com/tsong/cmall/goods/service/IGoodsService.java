package com.tsong.cmall.goods.service;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;

import java.util.List;

public interface IGoodsService {

    /**
     * @Description 获取商品详情
     * @Param [id]
     * @Return com.tsong.cmall.entity.GoodsInfo
     */
    GoodsInfo getGoodsInfoById(Long id);

    /**
     * @Description 商品搜索
     * @Param [pageUtil]
     * @Return com.tsong.cmall.util.PageResult
     */
    PageResult searchGoodsInfo(PageQueryUtil pageUtil);

    /**
     * @Description 输入框补全
     * @Param [prefix]
     * @Return java.util.List<java.lang.String>
     */
    List<String> getSuggestions(String prefix);

    List<GoodsInfo> getGoodsByIds(List<Long> ids);

    List<GoodsCategory> getGoodsCategoryByIds(List<Long> ids);

    int recoverStockNum(List<StockNumDTO> stockNumDTOS);
}
