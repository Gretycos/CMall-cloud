package com.tsong.cmall.seckill.mapper.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author Tsong
 * @Date 2023/9/4 23:45
 */
@Data
public class SeckillSuccessDTO {
    private Long seckillId;
    private Long userId;
    private Date createTime;
}
