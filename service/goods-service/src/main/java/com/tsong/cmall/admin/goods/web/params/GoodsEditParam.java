package com.tsong.cmall.admin.goods.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * @Author Tsong
 * @Date 2023/4/3 16:37
 */
@Data
public class GoodsEditParam {
    @Schema(title = "待修改商品id")
    @NotNull(message = "商品id不能为空")
    @Min(value = 1, message = "商品id不能为空")
    private Long goodsId;

    @Schema(title = "商品名称")
    @NotEmpty(message = "商品名称不能为空")
    @Length(max = 128,message = "商品名称内容过长")
    private String goodsName;

    @Schema(title = "商品简介")
    @NotEmpty(message = "商品简介不能为空")
    @Length(max = 200,message = "商品简介内容过长")
    private String goodsIntro;

    @Schema(title = "分类id")
    @NotNull(message = "分类id不能为空")
    @Min(value = 1, message = "分类id最低为1")
    private Long goodsCategoryId;

    @Schema(title = "商品主图")
    @NotEmpty(message = "商品主图不能为空")
    private String goodsCoverImg;

    @Schema(title = "商品轮播图")
    private String goodsCarousel;

    @Schema(title = "originalPrice")
    @NotNull(message = "originalPrice不能为空")
    @Min(value = 1, message = "originalPrice最低为1")
    @Max(value = 1000000, message = "originalPrice最高为1000000")
    private BigDecimal originalPrice;

    @Schema(title = "sellingPrice")
    @NotNull(message = "sellingPrice不能为空")
    @Min(value = 1, message = "sellingPrice最低为1")
    @Max(value = 1000000, message = "sellingPrice最高为1000000")
    private BigDecimal sellingPrice;

    @Schema(title = "库存")
    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存最低为1")
    @Max(value = 100000, message = "库存最高为100000")
    private Integer stockNum;

    @Schema(title = "商品标签")
    @NotEmpty(message = "商品标签不能为空")
    @Length(max = 16,message = "商品标签内容过长")
    private String tag;

    @Schema(title = "商品上架状态")
    @NotNull(message = "商品上架状态不能为空")
    @Min(value = 0, message = "不存在该状态")
    @Max(value = 1, message = "不存在该状态")
    private Byte goodsSaleStatus;

    @Schema(title = "商品详情")
    @NotEmpty(message = "商品详情不能为空")
    private String goodsDetailContent;
}
