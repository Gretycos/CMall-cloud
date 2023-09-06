package com.tsong.cmall.seckill.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT_DL;
import static com.tsong.cmall.common.constants.MQQueueCons.*;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.*;
import static com.tsong.cmall.seckill.enums.SeckillConfigEnum.SECKILL_STOCK_DECREASE_OVERTIME_MILLISECOND;
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
    public Queue stockDecreaseTtlQueue() { // 秒杀库存减少ttl队列
        return QueueBuilder
                .durable(SECKILL_STOCK_DECREASE_QUEUE) // ttl队列名
                .ttl(SECKILL_STOCK_DECREASE_OVERTIME_MILLISECOND.getTime()) // ms
                .deadLetterExchange(CMALL_DIRECT_DL)
                .deadLetterRoutingKey(SECKILL_STOCK_DECREASE_DL)
                .build();
    }

    @Bean
    public Queue stockRecoverTtlQueue() { // 秒杀库存恢复ttl队列
        return QueueBuilder
                .durable(SECKILL_STOCK_RECOVER_QUEUE) // ttl队列名
                .ttl(SECKILL_STOCK_RECOVER_OVERTIME_MILLISECOND.getTime()) // ms
                .deadLetterExchange(CMALL_DIRECT_DL)
                .deadLetterRoutingKey(SECKILL_STOCK_RECOVER_DL)
                .build();
    }

    @Bean
    public Queue seckillExpireQueue(){
        return new Queue(SECKILL_EXPIRE_QUEUE);
    }

    @Bean Queue stockDecreaseDlQueue() {
        return new Queue(SECKILL_STOCK_DECREASE_QUEUE_DL);
    }

    @Bean Queue stockRecoverDlQueue() {
        return new Queue(SECKILL_STOCK_RECOVER_QUEUE_DL);
    }

    @Bean
    public Binding stockDecreaseTtlBinding() {
        return BindingBuilder.bind(stockDecreaseTtlQueue()).to(directExchange()).with(SECKILL_STOCK_DECREASE);
    }

    @Bean
    public Binding stockDecreaseDlBinding() {
        return BindingBuilder.bind(stockDecreaseDlQueue()).to(dlDirectExchange()).with(SECKILL_STOCK_DECREASE_DL);
    }

    @Bean
    public Binding stockRecoverTtlBinding() {
        return BindingBuilder.bind(stockRecoverTtlQueue()).to(directExchange()).with(SECKILL_STOCK_RECOVER);
    }

    @Bean
    public Binding stockRecoverDlBinding() {
        return BindingBuilder.bind(stockRecoverDlQueue()).to(dlDirectExchange()).with(SECKILL_STOCK_RECOVER_DL);
    }

    @Bean
    public Binding seckillExpireBinding() {
        return BindingBuilder.bind(seckillExpireQueue()).to(directExchange()).with(SECKILL_EXPIRE);
    }
}
