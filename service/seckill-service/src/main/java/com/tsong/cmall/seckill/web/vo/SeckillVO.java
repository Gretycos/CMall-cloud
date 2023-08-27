package com.tsong.cmall.seckill.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Tsong
 * @Date 2023/5/10 19:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillVO {
    @Schema(title = "秒杀id")
    private Long seckillId;

    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品名")
    private String goodsName;

    @Schema(title = "商品封面图")
    private String goodsCoverImg;

    @Schema(title = "秒杀价格")
    private BigDecimal seckillPrice;

    @Schema(title = "数量")
    private Integer seckillNum;

    @Schema(title = "秒杀状态")
    private Boolean seckillStatus;

    @Schema(title = "秒杀开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date seckillBegin;

    @Schema(title = "秒杀结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date seckillEnd;

    @Schema(title = "秒杀排序值")
    private Integer seckillRank;

    @Schema(title = "秒杀创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(title = "秒杀更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
