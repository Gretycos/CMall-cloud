package com.tsong.cmall.coupon.mq.listener;

import com.tsong.cmall.coupon.service.ICouponService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.tsong.cmall.common.constants.MQQueueCons.COUPON_RECOVER_QUEUE;

/**
 * @Author Tsong
 * @Date 2023/8/20 18:42
 */
@Component
public class CouponMQListener {
    @Autowired
    private ICouponService couponService;

    @RabbitListener(queues = COUPON_RECOVER_QUEUE)
    public void couponRecover(Long orderId){
        couponService.releaseCoupon(orderId);
    }
}
