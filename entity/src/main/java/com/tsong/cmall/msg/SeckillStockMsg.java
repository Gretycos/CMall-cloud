package com.tsong.cmall.msg;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/9/1 00:22
 */
@Data
public class SeckillStockMsg implements Serializable {
    private Long userId;
    private Long seckillId;
}
