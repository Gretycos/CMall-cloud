package com.tsong.cmall.vo.shopping_cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ShoppingCartItemVO implements Serializable {
    @Schema(title = "购物车id")
    private Long cartItemId;

    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品数量")
    private Integer goodsCount;

    @Schema(title = "商品名")
    private String goodsName;

    @Schema(title = "商品封面图")
    private String goodsCoverImg;

    @Schema(title = "商品价格")
    private BigDecimal sellingPrice;
}
