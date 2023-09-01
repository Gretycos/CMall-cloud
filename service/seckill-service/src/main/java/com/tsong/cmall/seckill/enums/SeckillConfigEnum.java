package com.tsong.cmall.seckill.enums;

/**
 * @Author Tsong
 * @Date 2023/8/23 19:37
 */
public enum SeckillConfigEnum {
    SECKILL_STOCK_RECOVER_OVERTIME_MILLISECOND(120000); // 120s

    int time; //ms

    SeckillConfigEnum(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
