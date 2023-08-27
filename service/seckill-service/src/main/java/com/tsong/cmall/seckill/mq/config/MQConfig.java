package com.tsong.cmall.seckill.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT_DL;
import static com.tsong.cmall.common.constants.MQQueueCons.SECKILL_STOCK_RECOVER_QUEUE;
import static com.tsong.cmall.common.constants.MQQueueCons.SECKILL_STOCK_RECOVER_QUEUE_DL;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.SECKILL_STOCK_RECOVER;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.SECKILL_STOCK_RECOVER_DL;
import static com.tsong.cmall.seckill.enums.SeckillConfigEnum.SECKILL_STOCK_RECOVER_OVERTIME_MILLISECOND;

/**
 * @Author Tsong
 * @Date 2023/8/21 18:44
 */
@Configuration
public class MQConfig {
    @Bean
    public MessageConverter messageConverter(){
        // 发送方、接收方相同
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(CMALL_DIRECT);
    }

    @Bean
    public DirectExchange dlDirectExchange() {
        return new DirectExchange(CMALL_DIRECT_DL);
    }

    @Bean
    public Queue ttlQueue() { // 秒杀库存恢复ttl队列
        return QueueBuilder
                .durable(SECKILL_STOCK_RECOVER_QUEUE) // ttl队列名
                .ttl(SECKILL_STOCK_RECOVER_OVERTIME_MILLISECOND.getTime()) // ms
                .deadLetterExchange(CMALL_DIRECT_DL)
                .deadLetterRoutingKey(SECKILL_STOCK_RECOVER_DL)
                .build();
    }

    @Bean Queue dlQueue() {
        return new Queue(SECKILL_STOCK_RECOVER_QUEUE_DL);
    }

    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(directExchange()).with(SECKILL_STOCK_RECOVER);
    }

    @Bean
    public Binding dlBinding() {
        return BindingBuilder.bind(dlQueue()).to(dlDirectExchange()).with(SECKILL_STOCK_RECOVER_DL);
    }
}
