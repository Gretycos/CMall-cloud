package com.tsong.cmall.user;

import com.tsong.feign.clients.coupon.CouponClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Tsong
 * @Date 2023/8/19 17:17
 */

@EnableFeignClients(clients = CouponClient.class)
@MapperScan({
        "com.tsong.cmall.user.mapper",
        "com.tsong.cmall.admin.user.mapper"
})
@ComponentScan("com.tsong.cmall.*")
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
