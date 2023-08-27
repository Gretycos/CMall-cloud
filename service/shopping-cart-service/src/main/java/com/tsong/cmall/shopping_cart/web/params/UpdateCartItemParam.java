package com.tsong.cmall.shopping_cart.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

import static com.tsong.cmall.common.constants.Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER;

/**
 * @Author Tsong
 * @Date 2023/3/30 21:43
 */
@Data
public class UpdateCartItemParam implements Serializable {

    @Schema(title = "购物项id")
    @NotNull(message = "购物项id不能为空")
    private Long cartItemId;

    @Schema(title = "商品数量")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 0, message = "商品数量最小是0")
    @Max(value = SHOPPING_CART_ITEM_LIMIT_NUMBER, message = "商品数量最多是"+SHOPPING_CART_ITEM_LIMIT_NUMBER)
    private Integer goodsCount;
}
