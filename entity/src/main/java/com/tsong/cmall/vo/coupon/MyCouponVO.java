package com.tsong.cmall.vo.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 购物车所用的优惠券VO
 */
@Data
public class MyCouponVO implements Serializable {
    private static final long serialVersionUID = -8182785776876066101L;

    @Schema(title = "领券记录id")
    private Long couponUserId;

    @Schema(title = "用户id")
    private Long userId;

    @Schema(title = "优惠券id")
    private Long couponId;

    @Schema(title = "用户优惠券状态",description = "-1：失效，0：未使用，1：已使用")
    private Byte useStatus;

    @Schema(title = "优惠券名称")
    private String couponName;

    @Schema(title = "优惠券描述")
    private String couponDesc;

    @Schema(title = "优惠券抵用金额")
    private Integer discount;

    @Schema(title = "优惠券最低消费")
    private Integer min;

    @Schema(title = "商品限制类型")
    private Byte goodsType;

    @Schema(title = "商品限制值")
    private String goodsValue;

    @Schema(title = "商品限制值名称")
    private String goodsValueNames;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponUserCreateTime;
}
