package com.tsong.cmall.order.enums;

/**
 * @Author Tsong
 * @Date 2023/8/21 18:34
 */
public enum OrderConfigEnum {
    ORDER_UNPAID_OVERTIME_MILLISECOND(300000), // 300s
    ORDER_SECKILL_UNPAID_OVERTIME_MILLISECOND(120000); // 120s

    int time; //ms

    OrderConfigEnum(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
