package com.tsong.cmall.admin.coupon.web.params;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Tsong
 * @Date 2023/4/11 14:17
 */
@Data
public class CouponAddParam implements Serializable {
    @Schema(title = "优惠券名称")
    @NotEmpty(message = "优惠券名称不能为空")
    @Length(max = 32,message = "商品名称内容过长")
    private String couponName;

    @Schema(title = "优惠券简介")
    @NotEmpty(message = "优惠券简介不能为空")
    @Length(max = 200,message = "优惠券简介内容过长")
    private String couponDesc;

    @Schema(title = "优惠券数量")
    @NotNull(message = "优惠券数量不能为空")
    @Min(value = 0, message = "优惠券数量至少为0")
    private Integer couponTotal;

    @Schema(title = "优惠金额")
    @NotNull(message = "优惠金额不能为空")
    @Min(value = 0, message = "优惠金额至少为0")
    @Max(value = 10000, message = "优惠金额最多为10000")
    private Integer discount;

    @Schema(title = "最少消费金额")
    @NotNull(message = "最少消费金额不能为空")
    @Min(value = 0, message = "最少消费金额至少为0")
    @Max(value = 1000000, message = "优惠金额最多为10000")
    private Integer min;

    @Schema(title = "领券限制数量")
    @NotNull(message = "领券限制数量不能为空")
    @Max(value = 1, message = "超出有限制的领取数量")
    @Min(value = 0, message = "领取数量最少是0")
    private Byte couponLimit;

    @Schema(title = "优惠券类型")
    @NotNull(message = "优惠券类型不能为空")
    @Max(value = 2, message = "不存在该分类")
    @Min(value = 0, message = "不存在该分类")
    private Byte couponType;

    @Schema(title = "优惠券状态")
    @NotNull(message = "优惠券状态不能为空")
    @Max(value = 1, message = "不存在该状态")
    @Min(value = 0, message = "不存在该状态")
    private Byte couponStatus;

    @Schema(title = "商品限制类型")
    @NotNull(message = "可用商品类型不能为空")
    @Max(value = 2, message = "不存在该分类")
    @Min(value = 0, message = "不存在该分类")
    private Byte goodsType;

    @Schema(title = "商品限制值")
    private String goodsValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponEndTime;
}
