package com.tsong.cmall.order.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemVO implements Serializable {
    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品数量")
    private Integer goodsCount;

    @Schema(title = "商品名")
    private String goodsName;

    @Schema(title = "商品封面图片")
    private String goodsCoverImg;

    @Schema(title = "商品售价")
    private BigDecimal sellingPrice;

    @Schema(title = "实际付款")
    private BigDecimal paidPrice;
}
