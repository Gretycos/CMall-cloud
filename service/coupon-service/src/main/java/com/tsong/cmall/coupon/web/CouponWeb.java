package com.tsong.cmall.coupon.web;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.coupon.service.ICouponService;
import com.tsong.cmall.entity.Coupon;
import com.tsong.cmall.entity.UserCouponRecord;
import com.tsong.cmall.vo.coupon.MyCouponVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/22 13:14
 */
@RestController
@RequestMapping("/rpc/coupon")
public class CouponWeb {

    @Autowired
    private ICouponService couponService;

    @PostMapping("/send")
    @Operation(summary = "新用户送券", description = "rpc")
    public Result<Boolean> sendNewUserCoupons(@RequestParam Long userId){
        return ResultGenerator.genSuccessResult(couponService.sendNewUserCoupons(userId));
    }

    @GetMapping("/byUserCouponId")
    @Operation(summary = "通过领券id获取优惠券", description = "rpc")
    public Result<Coupon> getCouponByUserCouponId(@RequestParam Long id){
        return ResultGenerator.genSuccessResult(couponService.getByUserCouponId(id));
    }

    @PutMapping("/record")
    @Operation(summary = "更新用券记录", description = "rpc")
    public Result<Integer> updateUserCouponRecord(@RequestBody UserCouponRecord userCouponRecord){
        return ResultGenerator.genSuccessResult(couponService.updateUserCouponRecord(userCouponRecord));
    }

    @GetMapping("/byOrderId")
    @Operation(summary = "通过订单id获取优惠券", description = "rpc")
    public Result<Coupon> getCouponByOrderId(@RequestParam Long id){
        return ResultGenerator.genSuccessResult(couponService.getByOrderId(id));
    }

    @GetMapping("/forOrderConfirm")
    @Operation(summary = "为订单确认页面查询可用券", description = "rpc")
    public Result<List<MyCouponVO>> getCouponsForOrderConfirm(@RequestParam List<Long> shoppingCartGoodsIdList,
                                                              @RequestParam BigDecimal priceTotal,
                                                              @RequestParam Long userId){
        return ResultGenerator.genSuccessResult(
                couponService.selectCouponsForOrderConfirm(shoppingCartGoodsIdList, priceTotal, userId));
    }
}
