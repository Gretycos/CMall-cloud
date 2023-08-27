package com.tsong.cmall.coupon.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/5/25 01:11
 */
@Data
public class AddCouponParam implements Serializable {
    @Schema(title = "优惠券id")
    Long couponId;
    @Schema(title = "兑换码")
    String couponCode;
}
