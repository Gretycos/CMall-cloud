package com.tsong.feign.clients.coupon.fallback;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.UserCouponRecord;
import com.tsong.cmall.vo.ShoppingCartItemVO;
import com.tsong.feign.clients.coupon.CouponClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/20 16:44
 */
public class CouponClientFallbackFactory implements FallbackFactory<CouponClient> {
    @Override
    public CouponClient create(Throwable cause) {
        return new CouponClient() {
            Result result = ResultGenerator.genFailResult(cause.getMessage());

            @Override
            public Result sendNewUserCoupons(Long userId) {
                return result;
            }

            @Override
            public Result getCouponByCouponUserId(Long id) {
                return result;
            }

            @Override
            public Result updateUserCouponRecord(UserCouponRecord userCouponRecord) {
                return result;
            }

            @Override
            public Result getCouponByOrderId(Long id) {
                return result;
            }

            @Override
            public Result getCouponsForOrderConfirm(List<Long> shoppingCartGoodsIdList, BigDecimal priceTotal, Long userId) {
                return result;
            }
        };
    }
}
