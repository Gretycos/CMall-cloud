package com.tsong.cmall.seckill.mq.listener;

import com.rabbitmq.client.Channel;
import com.tsong.cmall.msg.SeckillStockMsg;
import com.tsong.cmall.seckill.service.ISeckillService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.tsong.cmall.common.constants.MQQueueCons.SECKILL_STOCK_RECOVER_QUEUE_DL;

/**
 * @Author Tsong
 * @Date 2023/8/23 22:32
 */
@Component
public class SeckillMQListener {
    @Autowired
    private ISeckillService seckillService;

    @RabbitListener(queues = SECKILL_STOCK_RECOVER_QUEUE_DL)
    public void handleSeckillStockRecover(SeckillStockMsg seckillStockMsg,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        Long userId = seckillStockMsg.getUserId();
        Long seckillId = seckillStockMsg.getSeckillId();
        seckillService.stockRecover(userId, seckillId);
        channel.basicAck(deliveryTag, false);
    }
}
