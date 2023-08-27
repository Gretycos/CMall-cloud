package com.tsong.cmall.shopping_cart.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/3/30 21:42
 */
@Data
public class SaveCartItemParam implements Serializable {

    @Schema(title = "商品数量")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 0, message = "商品数量最小是0")
    private Integer goodsCount;

    @Schema(title = "商品id")
    @NotNull(message = "商品id不能为空")
    private Long goodsId;
}
