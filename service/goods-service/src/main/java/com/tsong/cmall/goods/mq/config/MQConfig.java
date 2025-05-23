package com.tsong.cmall.goods.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQQueueCons.*;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.*;


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
    public DirectExchange directExchange(){
        return new DirectExchange(CMALL_DIRECT);
    }

    @Bean
    public Queue recoverQueue(){
        return new Queue(GOODS_STOCK_RECOVER_QUEUE);
    }

    @Bean
    public Queue stockDecreaseQueue(){
        return new Queue(GOODS_STOCK_DECREASE_QUEUE);
    }

    @Bean
    public Queue changedQueue(){
        return new Queue(GOODS_CHANGED_QUEUE);
    }

    @Bean
    public Binding recoverBinding(){
        return BindingBuilder.bind(recoverQueue()).to(directExchange()).with(GOODS_STOCK_RECOVER);
    }

    @Bean
    public Binding stockDecreaseBinding(){
        return BindingBuilder.bind(stockDecreaseQueue()).to(directExchange()).with(GOODS_STOCK_DECREASE);
    }

    @Bean
    public Binding changedBinding(){
        return BindingBuilder.bind(changedQueue()).to(directExchange()).with(GOODS_CHANGED);
    }
}
