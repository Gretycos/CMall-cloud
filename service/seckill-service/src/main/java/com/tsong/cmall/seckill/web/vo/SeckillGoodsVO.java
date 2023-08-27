package com.tsong.cmall.seckill.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Tsong
 * @Date 2023/3/25 14:08
 */
@Data
public class SeckillGoodsVO implements Serializable {
    private static final long serialVersionUID = -8719192110998138980L;

    @Schema(title = "秒杀id")
    private Long seckillId;

    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品名")
    private String goodsName;

    @Schema(title = "商品简介")
    private String goodsIntro;

    @Schema(title = "商品细节")
    private String goodsDetailContent;

    @Schema(title = "商品封面图")
    private String goodsCoverImg;

    @Schema(title = "商品轮播图")
    private String[] goodsCarousel;

    @Schema(title = "商品售价")
    private BigDecimal sellingPrice;

    @Schema(title = "商品秒杀价")
    private BigDecimal seckillPrice;

    @Schema(title = "秒杀开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date seckillBegin;

    @Schema(title = "秒杀结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date seckillEnd;
}
