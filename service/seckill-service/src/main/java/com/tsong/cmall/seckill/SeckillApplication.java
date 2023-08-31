package com.tsong.cmall.seckill;

import com.tsong.feign.clients.goods.GoodsClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Tsong
 * @Date 2023/8/22 14:56
 */
@EnableFeignClients(clients = GoodsClient.class)
@MapperScan("com.tsong.cmall.seckill.mapper")
@ComponentScan("com.tsong.cmall.*")
@SpringBootApplication
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }
}
