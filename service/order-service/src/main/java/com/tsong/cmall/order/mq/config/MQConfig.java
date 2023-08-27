package com.tsong.cmall.order.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT_DL;
import static com.tsong.cmall.common.constants.MQQueueCons.*;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.*;
import static com.tsong.cmall.order.enums.OrderConfigEnum.ORDER_SECKILL_UNPAID_OVERTIME_MILLISECOND;
import static com.tsong.cmall.order.enums.OrderConfigEnum.ORDER_UNPAID_OVERTIME_MILLISECOND;

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
    public DirectExchange directExchange(){
        return new DirectExchange(CMALL_DIRECT);
    }

    @Bean
    public DirectExchange dlDirectExchange() {
        return new DirectExchange(CMALL_DIRECT_DL);
    }

    @Bean
    public Queue ttlQueue() {
        return QueueBuilder
                .durable(ORDER_UNPAID_QUEUE) // ttl队列名
                .ttl(ORDER_UNPAID_OVERTIME_MILLISECOND.getTime()) // ms
                .deadLetterExchange(CMALL_DIRECT_DL)
                .deadLetterRoutingKey(ORDER_UNPAID_DL)
                .build();
    }

    @Bean
    public Queue ttlSeckillQueue() {
        return QueueBuilder
                .durable(ORDER_SECKILL_UNPAID_QUEUE) // ttl队列名
                .ttl(ORDER_SECKILL_UNPAID_OVERTIME_MILLISECOND.getTime()) // ms
                .deadLetterExchange(CMALL_DIRECT_DL)
                .deadLetterRoutingKey(ORDER_UNPAID_DL)
                .build();
    }

    @Bean Queue dlQueue() {
        return new Queue(ORDER_UNPAID_QUEUE_DL);
    }

    @Bean Queue seckillOrderCreateQueue(){
        return new Queue(ORDER_SECKILL_CREATE_QUEUE);
    }

    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(directExchange()).with(ORDER_UNPAID);
    }

    @Bean
    public Binding ttlSeckillBinding() {
        return BindingBuilder.bind(ttlQueue()).to(directExchange()).with(ORDER_SECKILL_UNPAID);
    }

    @Bean
    public Binding dlBinding() {
        return BindingBuilder.bind(dlQueue()).to(dlDirectExchange()).with(ORDER_UNPAID_DL);
    }

    @Bean
    public Binding seckillOrderCreateBinding(){
        return BindingBuilder.bind(seckillOrderCreateQueue()).to(directExchange()).with(ORDER_SECKILL_CREATE);
    }
}
