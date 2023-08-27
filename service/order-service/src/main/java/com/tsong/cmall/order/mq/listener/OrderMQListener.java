package com.tsong.cmall.order.mq.listener;

import com.tsong.cmall.msg.CreateSeckillOrderMsg;
import com.tsong.cmall.order.enums.PayStatusEnum;
import com.tsong.cmall.order.mapper.dto.OrderPayStatusDTO;
import com.tsong.cmall.order.service.IOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.tsong.cmall.common.constants.MQQueueCons.ORDER_SECKILL_UNPAID_QUEUE;
import static com.tsong.cmall.common.constants.MQQueueCons.ORDER_UNPAID_QUEUE_DL;

/**
 * @Author Tsong
 * @Date 2023/8/21 18:40
 */
@Component
public class OrderMQListener {
    @Autowired
    private IOrderService orderService;

    /**
     * @Description 订单超时未支付
     * @Param [orderId]
     * @Return void
     */
    @RabbitListener(queues = ORDER_UNPAID_QUEUE_DL)
    public void handleUnpaidOrder(Long orderId){
        OrderPayStatusDTO payStatus = orderService.getOrderStatus(orderId);
        // 最后都未支付
        if (payStatus.getPayStatus() == PayStatusEnum.PAY_PAYING.getPayStatus()){
            orderService.handleUnpaidOrder(orderId);
        }
    }

    /**
     * @Description 创建新秒杀订单
     * @Param [msg]
     * @Return void
     */
    @RabbitListener(queues = ORDER_SECKILL_UNPAID_QUEUE)
    public void handleOrderSeckillCreate(CreateSeckillOrderMsg msg){
        Long userId = msg.getUserId();
        Long seckillId = msg.getSeckillId();
        Long goodsId = msg.getGoodsId();
        Long addressId = msg.getAddressId();
        BigDecimal seckillPrice = msg.getSeckillPrice();
        orderService.handleSeckillSaveOrder(userId, seckillId, goodsId, addressId, seckillPrice);
    }
}
