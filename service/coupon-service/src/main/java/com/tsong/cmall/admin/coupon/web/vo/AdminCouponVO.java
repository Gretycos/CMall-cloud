package com.tsong.cmall.admin.coupon.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Tsong
 * @Date 2023/5/29 15:32
 */
@Data
public class AdminCouponVO implements Serializable {
    @Schema(title = "优惠券id")
    private Long couponId;

    @Schema(title = "优惠券名字")
    private String couponName;

    @Schema(title = "优惠券描述")
    private String couponDesc;

    @Schema(title = "优惠券总数")
    private Integer couponTotal;

    @Schema(title = "优惠券折扣")
    private Integer discount;

    @Schema(title = "优惠券最小使用金额")
    private Integer min;

    @Schema(title = "优惠券限制领取数量")
    private Byte couponLimit;

    @Schema(title = "优惠券类型")
    private Byte couponType;

    @Schema(title = "优惠券状态")
    private Byte couponStatus;

    @Schema(title = "可用类型")
    private Byte goodsType;

    @Schema(title = "可用商品/分类")
    private String goodsValue;

    @Schema(title = "可用商品/分类名称")
    private String goodsValueNames;

    @Schema(title = "优惠券兑换码")
    private String couponCode;

    @Schema(title = "优惠券开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "优惠券结束时间")
    private Date couponEndTime;

    @Schema(title = "优惠券创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(title = "优惠券更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
