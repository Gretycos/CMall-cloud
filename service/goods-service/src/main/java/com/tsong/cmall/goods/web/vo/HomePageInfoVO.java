package com.tsong.cmall.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/31 22:57
 */
@Data
public class HomePageInfoVO implements Serializable {
    @Schema(title = "轮播图(列表)")
    private List<HomePageCarouselVO> carouselList;

    @Schema(title = "首页热销商品(列表)")
    private List<HomePageConfigGoodsVO> hotGoodsList;

    @Schema(title = "首页新品推荐(列表)")
    private List<HomePageConfigGoodsVO> newGoodsList;

    @Schema(title = "首页推荐商品(列表)")
    private List<HomePageConfigGoodsVO> recommendGoodsList;
}
