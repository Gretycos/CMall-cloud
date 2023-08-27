package com.tsong.cmall.common.constants;

/**
 * @Author Tsong
 * @Date 2023/8/23 23:37
 */
public class MQQueueCons {
    public static final String ORDER_UNPAID_QUEUE = "order.unpaid.queue";
    public static final String ORDER_SECKILL_UNPAID_QUEUE = "order.seckill.unpaid.queue";
    public static final String ORDER_UNPAID_QUEUE_DL = "order.unpaid.queue.dl"; // 订单死信队列
    public static final String ORDER_SECKILL_CREATE_QUEUE = "order.seckill.unpaid.queue";
    public static final String SECKILL_STOCK_RECOVER_QUEUE = "seckill.stock.recover.queue"; // 秒杀库存恢复死信队列
    public static final String SECKILL_STOCK_RECOVER_QUEUE_DL = "seckill.stock.recover.queue.dl"; // 秒杀库存恢复死信队列
    public static final String COUPON_RECOVER_QUEUE = "coupon.recover.queue";
    public static final String GOODS_STOCK_RECOVER_QUEUE = "goods.stock.recover.queue";
}
