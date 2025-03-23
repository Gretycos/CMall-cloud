package com.tsong.cmall.order.mq.listener;

import com.rabbitmq.client.Channel;
import com.tsong.cmall.msg.CreateSeckillOrderMsg;
import com.tsong.cmall.order.enums.PayStatusEnum;
import com.tsong.cmall.order.mapper.dto.OrderPayStatusDTO;
import com.tsong.cmall.order.service.IOrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static com.tsong.cmall.common.constants.MQQueueCons.*;

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
    public void handleUnpaidOrder(Long orderId,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        OrderPayStatusDTO payStatus = orderService.getOrderStatus(orderId);
        // 最后都未支付
        if (payStatus.getPayStatus() == PayStatusEnum.PAY_PAYING.getPayStatus()){
            orderService.handleUnpaidOrder(orderId);
        }
        channel.basicAck(deliveryTag, false);
    }

    /**
     * @Description 创建新秒杀订单
     * @Param [msg]
     * @Return void
     */
    @RabbitListener(queues = ORDER_SECKILL_CREATE_QUEUE)
    public void handleOrderSeckillCreate(CreateSeckillOrderMsg msg,
                                         Channel channel,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        Long userId = msg.getUserId();
        Long seckillId = msg.getSeckillId();
        Long goodsId = msg.getGoodsId();
        Long addressId = msg.getAddressId();
        BigDecimal seckillPrice = msg.getSeckillPrice();
        orderService.handleSeckillSaveOrder(userId, seckillId, goodsId, addressId, seckillPrice);
        channel.basicAck(deliveryTag, false);
    }
}
