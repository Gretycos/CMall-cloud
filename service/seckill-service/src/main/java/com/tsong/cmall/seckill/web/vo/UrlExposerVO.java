package com.tsong.cmall.seckill.web.vo;

import com.tsong.cmall.seckill.enums.SeckillStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UrlExposerVO implements Serializable {
    private static final long serialVersionUID = -7615136662052646516L;
    @Schema(title = "秒杀状态enum")
    private SeckillStatusEnum seckillStatusEnum;

    @Schema(title = "一种加密措施")
    private String md5;

    @Schema(title = "秒杀id")
    private long seckillId;

    @Schema(title = "系统当前时间（毫秒）")
    private long now;

    @Schema(title = "开启时间")
    private long start;

    @Schema(title = "结束时间")
    private long end;

    public UrlExposerVO(SeckillStatusEnum seckillStatusEnum, String md5, long seckillId) {
        this.seckillStatusEnum = seckillStatusEnum;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public UrlExposerVO(SeckillStatusEnum seckillStatusEnum, long seckillId, long now, long start, long end) {
        this.seckillStatusEnum = seckillStatusEnum;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public UrlExposerVO(SeckillStatusEnum seckillStatusEnum, long seckillId) {
        this.seckillStatusEnum = seckillStatusEnum;
        this.seckillId = seckillId;
    }
}
