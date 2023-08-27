package com.tsong.cmall.seckill.mq.listener;

import com.tsong.cmall.seckill.service.ISeckillService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void handleSeckillStockRecover(Map<String, Object> msg){
        Long userId = (Long) msg.get("userId");
        Long seckillId = (Long) msg.get("seckillId");
        seckillService.stockRecover(userId, seckillId);
    }
}
