package com.tsong.feign.clients.coupon;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.UserCouponRecord;
import com.tsong.cmall.vo.ShoppingCartItemVO;
import com.tsong.feign.clients.coupon.fallback.CouponClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;
import java.util.List;


/**
 * @Author Tsong
 * @Date 2023/8/20 15:42
 */
@FeignClient(value = "coupon-service", fallbackFactory = CouponClientFallbackFactory.class)
public interface CouponClient {

    String RPC_SUFFIX = "/rpc/coupon";

    @PostMapping(RPC_SUFFIX + "/send")
    Result sendNewUserCoupons(Long userId);

    @GetMapping(RPC_SUFFIX + "/byUserCouponId")
    Result getCouponByCouponUserId(Long id);

    @PutMapping(RPC_SUFFIX + "/record")
    Result updateUserCouponRecord(UserCouponRecord userCouponRecord);

    @GetMapping(RPC_SUFFIX + "/byOrderId")
    Result getCouponByOrderId(Long id);

    @GetMapping(RPC_SUFFIX + "/forOrderConfirm")
    Result getCouponsForOrderConfirm(List<ShoppingCartItemVO> shoppingCartItemVOList, BigDecimal priceTotal, Long userId);
}
