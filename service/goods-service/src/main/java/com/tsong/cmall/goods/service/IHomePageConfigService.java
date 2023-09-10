package com.tsong.cmall.goods.service;


import com.tsong.cmall.goods.web.vo.HomePageCategoryVO;
import com.tsong.cmall.goods.web.vo.HomePageInfoVO;

import java.util.List;

public interface IHomePageConfigService {
    /**
     * @Description 返回固定数量的首页配置商品对象(首页调用)
     * @Param [configType, number]
     * @Return java.util.List<HomePageConfigGoodsVO>
     */
    HomePageInfoVO getHomePageInfo();

    /**
     * @Description 首页分类数据
     * @Param []
     * @Return java.util.List<com.tsong.cmall.controller.vo.HomePageCategoryVO>
     */
    List<HomePageCategoryVO> getCategoriesForHomePage();

}
