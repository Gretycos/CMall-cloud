package com.tsong.cmall.common.mq;

import lombok.AllArgsConstructor;

/**
 * @Author Tsong
 * @Date 2025/4/13 18:36
 */
@AllArgsConstructor
public class MyMsg {
    String exchange;
    String routingKey;
    Object data;
}
