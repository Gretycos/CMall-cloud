package com.tsong.cmall.goods.mq.listener;

import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.goods.service.IGoodsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void goodsStockRecover(List<StockNumDTO> stockNumDTOS){
        goodsService.recoverStockNum(stockNumDTOS);
    }
}
