package com.tsong.cmall.order;

import com.tsong.feign.clients.coupon.CouponClient;
import com.tsong.feign.clients.goods.GoodsClient;
import com.tsong.feign.clients.shopping_cart.ShoppingCartClient;
import com.tsong.feign.clients.user.address.AddressClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Tsong
 * @Date 2023/8/20 22:52
 */
@EnableFeignClients(clients = {ShoppingCartClient.class, GoodsClient.class, CouponClient.class, AddressClient.class})
@MapperScan("com.tsong.cmall.order.mapper")
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
