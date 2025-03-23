package com.tsong.cmall.goods.mq.listener;

import com.rabbitmq.client.Channel;
import com.tsong.cmall.admin.goods.service.IAdminGoodsService;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.goods.service.IGoodsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.tsong.cmall.common.constants.MQQueueCons.*;

/**
 * @Author Tsong
 * @Date 2023/8/20 18:42
 */
@Component
public class GoodsMQListener {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IAdminGoodsService adminGoodsService;

    @RabbitListener(queues = GOODS_STOCK_RECOVER_QUEUE)
    public void goodsStockRecover(List<StockNumDTO> stockNumDTOS,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        goodsService.recoverStockNum(stockNumDTOS);
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(queues = GOODS_STOCK_DECREASE_QUEUE)
    public void goodsStockDecrease(List<StockNumDTO> stockNumDTOS,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        goodsService.decreaseStockNum(stockNumDTOS);
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(queues = GOODS_CHANGED_QUEUE)
    public void goodsChanged(Map<String, Object> params,
                             Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        switch ((int)params.get("op")) {
            case 0 -> adminGoodsService.deleteGoodsES((GoodsInfo) params.get("data"));
            case 1 -> adminGoodsService.insertGoodsES((GoodsInfo) params.get("data"));
            case 2 -> adminGoodsService.updateGoodsES((GoodsInfo) params.get("data"));
        }
        channel.basicAck(deliveryTag, false);
    }
}
