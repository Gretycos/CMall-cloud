package com.tsong.cmall.admin.seckill.web.params;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Tsong
 * @Date 2023/4/12 14:10
 */
@Data
public class SeckillAddParam implements Serializable {

    @Schema(title = "商品id")
    @NotNull(message = "商品id不能为空")
    private Long goodsId;

    @Schema(title = "秒杀价格")
    @NotNull(message = "秒杀价格不能为空")
    @Min(value = 0, message = "秒杀价格至少为0")
    private BigDecimal seckillPrice;

    @Schema(title = "秒杀数量")
    @NotNull(message = "秒杀数量不能为空")
    @Min(value = 1, message = "秒杀数量至少为1")
    private Integer seckillNum;

    @Schema(title = "秒杀状态")
    @NotNull(message = "秒杀状态不能为空")
    private Boolean seckillStatus;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date seckillBegin;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date seckillEnd;

    @Schema(title = "秒杀排序值")
    @NotNull(message = "秒杀排序值不能为空")
    @Max(value = 999, message = "排序值过大")
    @Min(value = 0, message = "排序值至少为0")
    private Integer seckillRank;
}
