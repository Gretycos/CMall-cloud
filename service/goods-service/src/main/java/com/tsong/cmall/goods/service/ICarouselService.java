package com.tsong.cmall.goods.service;

import com.tsong.cmall.goods.web.vo.HomePageCarouselVO;

import java.util.List;

public interface ICarouselService {
    /**
     * @Description 返回固定数量的轮播图对象(首页调用)
     * @Param [number]
     * @Return java.util.List<com.tsong.cmall.controller.vo.HomePageCarouselVO>
     */
    List<HomePageCarouselVO> getCarouselsForHomePage(int number);
}
