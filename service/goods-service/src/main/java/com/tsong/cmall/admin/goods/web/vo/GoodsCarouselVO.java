package com.tsong.cmall.admin.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/5/30 15:26
 */
@Data
public class GoodsCarouselVO implements Serializable {
    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品轮播图")
    private String goodsCarousel;
}
