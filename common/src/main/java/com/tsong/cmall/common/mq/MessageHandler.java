package com.tsong.cmall.common.mq;

import com.tsong.cmall.common.exception.CMallException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author Tsong
 * @Date 2023/8/20 17:55
 */

@Component
@Slf4j
public class MessageHandler {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private BatchingRabbitTemplate batchingRabbitTemplate;

    public void sendMessage(String exchange, String routingKey, Object data) throws CMallException{
        // 准备CorrelationData
        CorrelationData correlationData = getCorrelationData();
        rabbitTemplate.convertAndSend(exchange, routingKey, data, correlationData);
        try {
            correlationData.getFuture().join();
        } catch (Exception e) {
            CMallException.fail(e.getMessage());
        }
    }

    public void sendMessageBatch(List<MyMsg> msgList){
        List<CompletableFuture<CorrelationData.Confirm>> futures = new ArrayList<>();
        for (MyMsg myMsg : msgList) {
            // 准备CorrelationData
            CorrelationData correlationData = getCorrelationData();
            // 有一条失败就全都nack
            batchingRabbitTemplate.convertAndSend(myMsg.exchange, myMsg.routingKey, myMsg.data, correlationData);
            futures.add(correlationData.getFuture());
        }
        try {
            for (CompletableFuture<CorrelationData.Confirm> future : futures) {
                future.join();
            }
        }catch (Exception e) {
            CMallException.fail(e.getMessage());
        }
    }

    private CorrelationData getCorrelationData() {
        // 准备CorrelationData
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 处理confirm
        correlationData.getFuture().whenComplete((confirm, throwable) -> {
            if (throwable == null){
                if (confirm.isAck()){
                    // ACK
                    log.debug("Succeeded to send the msg to the exchange! Msg ID: {}", correlationData.getId());
                } else {
                    // NACK
                    log.error("Failed to send the msg to the exchange! Msg ID: {}", correlationData.getId());
                    CMallException.fail("Failed to send the msg to the exchange");
                }
            } else {
                log.error("Failed to send the msg! {}", throwable.getMessage());
                CMallException.fail("Failed to send the msg");
            }
        });
        return correlationData;
    }
}
