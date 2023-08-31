package com.tsong.feign.clients.coupon;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.Coupon;
import com.tsong.cmall.entity.UserCouponRecord;
import com.tsong.cmall.vo.MyCouponVO;
import com.tsong.cmall.vo.ShoppingCartItemVO;
import com.tsong.feign.clients.coupon.fallback.CouponClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
    Result<Boolean> sendNewUserCoupons(@RequestParam Long userId);

    @GetMapping(RPC_SUFFIX + "/byUserCouponId")
    Result<Coupon> getCouponByCouponUserId(@RequestParam Long id);

    @PutMapping(RPC_SUFFIX + "/record")
    Result<Integer> updateUserCouponRecord(@RequestBody UserCouponRecord userCouponRecord);

    @GetMapping(RPC_SUFFIX + "/byOrderId")
    Result<Coupon> getCouponByOrderId(@RequestParam Long id);

    @GetMapping(RPC_SUFFIX + "/forOrderConfirm")
    Result<List<MyCouponVO>> getCouponsForOrderConfirm(@RequestParam List<Long> shoppingCartGoodsIdList,
                                                       @RequestParam BigDecimal priceTotal,
                                                       @RequestParam Long userId);
}
