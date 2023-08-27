package com.tsong.cmall.msg;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建秒杀订单消息
 * @Author Tsong
 * @Date 2023/8/24 00:36
 */
@Data
public class CreateSeckillOrderMsg implements Serializable {
    private Long userId;
    private Long seckillId;
    private Long goodsId;
    private Long addressId;
    private BigDecimal seckillPrice;

}
