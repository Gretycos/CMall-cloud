package com.tsong.cmall.order.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/3/31 23:42
 */
@Data
public class SaveOrderParam implements Serializable {
    @Schema(title = "订单项id数组")
    @NotNull(message = "物品id列表不能为空")
    private Long[] cartItemIds;

    @Schema(title = "领券记录id")
    private Long couponUserId;

    @Schema(title = "地址id")
    @NotNull(message = "地址id不能为空")
    private Long addressId;
}
