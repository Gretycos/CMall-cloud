package com.tsong.cmall.shopping_cart;

import com.tsong.feign.clients.coupon.CouponClient;
import com.tsong.feign.clients.goods.GoodsClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Tsong
 * @Date 2023/8/22 11:29
 */
@EnableFeignClients(clients = {CouponClient.class, GoodsClient.class})
@MapperScan("com.tsong.cmall.shopping_cart.mapper")
@SpringBootApplication
public class ShoppingCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }
}
