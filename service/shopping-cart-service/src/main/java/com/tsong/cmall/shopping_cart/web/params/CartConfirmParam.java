package com.tsong.cmall.shopping_cart.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/5/17 21:24
 */
@Data
public class CartConfirmParam implements Serializable {
    @Schema(title = "id数组")
    @NotNull(message = "id数组不能为空")
    Long[] cartItemIds;
}
