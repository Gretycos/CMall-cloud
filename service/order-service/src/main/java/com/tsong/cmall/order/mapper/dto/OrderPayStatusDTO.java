package com.tsong.cmall.order.mapper.dto;

import lombok.Data;

/**
 * @Author Tsong
 * @Date 2023/8/21 19:33
 */
@Data
public class OrderPayStatusDTO {
    private Long orderId;

    private Byte payStatus;
}
