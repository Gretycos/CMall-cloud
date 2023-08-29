package com.tsong.cmall.goods.service;


import com.tsong.cmall.goods.web.vo.HomePageInfoVO;

public interface IHomePageConfigService {
    /**
     * @Description 返回固定数量的首页配置商品对象(首页调用)
     * @Param [configType, number]
     * @Return java.util.List<HomePageConfigGoodsVO>
     */
    HomePageInfoVO getHomePageInfo();

}
