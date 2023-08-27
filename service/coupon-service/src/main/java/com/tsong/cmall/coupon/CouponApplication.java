package com.tsong.cmall.coupon;

import com.tsong.feign.clients.goods.GoodsClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Tsong
 * @Date 2023/8/20 14:05
 */
@EnableFeignClients(clients = GoodsClient.class)
@MapperScan("com.tsong.cmall.coupon.mapper")
@SpringBootApplication
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
}
