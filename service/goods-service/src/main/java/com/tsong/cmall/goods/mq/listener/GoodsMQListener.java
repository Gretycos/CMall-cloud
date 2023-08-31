package com.tsong.cmall.goods.mq.listener;

import com.rabbitmq.client.Channel;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.goods.service.IGoodsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.tsong.cmall.common.constants.MQQueueCons.GOODS_STOCK_RECOVER_QUEUE;

/**
 * @Author Tsong
 * @Date 2023/8/20 18:42
 */
@Component
public class GoodsMQListener {
    @Autowired
    private IGoodsService goodsService;

    @RabbitListener(queues = GOODS_STOCK_RECOVER_QUEUE)
    public void goodsStockRecover(List<StockNumDTO> stockNumDTOS,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        goodsService.recoverStockNum(stockNumDTOS);
        channel.basicAck(deliveryTag, false);
    }
}
