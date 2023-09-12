package com.tsong.cmall.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Tsong
 * @Date 2023/8/20 15:01
 */
@MapperScan({
        "com.tsong.cmall.goods.mapper",
        "com.tsong.cmall.admin.goods.mapper"
})
@ComponentScan(value = {
        "com.tsong.cmall.admin.goods.*",
        "com.tsong.cmall.goods.*",
        "com.tsong.cmall.common.exception"
})
@SpringBootApplication
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }
}
