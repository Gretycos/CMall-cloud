package com.tsong.cmall.seckill.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class SeckillSuccessVO implements Serializable {
    private static final long serialVersionUID = 1503814153626594835L;

//    @Schema(title = "秒杀成功id")
//    private Long seckillSuccessId;

    @Schema(title = "秒杀加密信息")
    private String md5;
}
