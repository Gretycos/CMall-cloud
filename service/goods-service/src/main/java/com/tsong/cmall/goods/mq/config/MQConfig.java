package com.tsong.cmall.goods.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQQueueCons.GOODS_STOCK_RECOVER_QUEUE;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.GOODS_STOCK_RECOVER;


/**
 * @Author Tsong
 * @Date 2023/8/22 00:14
 */
@Configuration
public class MQConfig {
    @Bean
    public MessageConverter messageConverter(){
        // 发送方、接收方相同
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange topicExchange(){
        return new DirectExchange(CMALL_DIRECT);
    }

    @Bean
    public Queue recoverQueue(){
        return new Queue(GOODS_STOCK_RECOVER_QUEUE);
    }

    @Bean
    public Binding recoverBinding(){
        return BindingBuilder.bind(recoverQueue()).to(topicExchange()).with(GOODS_STOCK_RECOVER);
    }
}
