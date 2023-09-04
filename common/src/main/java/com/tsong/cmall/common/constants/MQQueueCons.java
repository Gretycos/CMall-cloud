package com.tsong.cmall.common.constants;

/**
 * @Author Tsong
 * @Date 2023/8/23 23:37
 */
public class MQQueueCons {
    public static final String ORDER_UNPAID_QUEUE = "order.unpaid.queue"; // 订单未支付队列
    public static final String ORDER_SECKILL_UNPAID_QUEUE = "order.seckill.unpaid.queue"; // 秒杀订单未支付队列
    public static final String ORDER_UNPAID_QUEUE_DL = "order.unpaid.queue.dl"; // 订单和秒杀订单的死信队列
    public static final String ORDER_SECKILL_CREATE_QUEUE = "order.seckill.create.queue"; // 秒杀订单创建队列

    public static final String SECKILL_STOCK_DECREASE_QUEUE = "seckill.stock.decrease.queue"; // 秒杀库存减少队列
    public static final String SECKILL_STOCK_DECREASE_QUEUE_DL = "seckill.stock.decrease.queue.dl"; // 秒杀库存减少死信队列
    public static final String SECKILL_STOCK_RECOVER_QUEUE = "seckill.stock.recover.queue"; // 秒杀库存恢复队列
    public static final String SECKILL_STOCK_RECOVER_QUEUE_DL = "seckill.stock.recover.queue.dl"; // 秒杀库存恢复死信队列

    public static final String COUPON_RECOVER_QUEUE = "coupon.recover.queue";
    public static final String GOODS_STOCK_RECOVER_QUEUE = "goods.stock.recover.queue";
}
