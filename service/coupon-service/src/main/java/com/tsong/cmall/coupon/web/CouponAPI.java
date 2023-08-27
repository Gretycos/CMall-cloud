package com.tsong.cmall.coupon.web;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.coupon.service.ICouponService;
import com.tsong.cmall.coupon.web.params.AddCouponParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/4/6 15:58
 */
@RestController
@Tag(name = "coupon", description = "1-8.优惠券页面接口")
@RequestMapping("/api/coupon")
public class CouponAPI {
    @Autowired
    private ICouponService couponService;

    @GetMapping("/available/list")
    @Operation(summary = "可领优惠券列表", description = "")
    public Result availableCouponList(@Parameter(name = "页码") @RequestParam(required = false) Integer pageNumber,
                                      Long userId){
        Map<String, Object> params = new HashMap<>(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("page", pageNumber);
        params.put("limit", Constants.MY_COUPONS_LIMIT);
        // 封装优惠券分页查询参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);

        return ResultGenerator.genSuccessResult(couponService.selectAvailableCoupons(userId, pageUtil));
    }

    @GetMapping("/my")
    @Operation(summary = "我的优惠券列表", description = "能够查询到领券后一个月内的记录")
    public Result myCouponList(@Parameter(name = "页码") @RequestParam(required = false) Integer pageNumber,
                                                     @Parameter(name = "使用状态") @RequestParam(required = false) Byte useStatus,
                               Long userId){
        Map<String, Object> params = new HashMap<>(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", userId);
        params.put("page", pageNumber);
        params.put("limit", Constants.MY_COUPONS_LIMIT);
        params.put("useStatus", useStatus);
        // 封装优惠券分页查询参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);

        return ResultGenerator.genSuccessResult(couponService.selectMyCoupons(pageUtil));
    }

    @GetMapping("/my/available")
    @Operation(summary = "我的所有可用优惠券", description = "能够查询到领券后一个月内的记录")
    public Result allMyAvailableCouponList(Long userId){
        return ResultGenerator.genSuccessResult(couponService.selectAllMyAvailableCoupons(userId));
    }

    @PostMapping("/save")
    @Operation(summary = "领券", description = "传参为优惠券id，优惠券兑换码（可选）")
    public Result saveCoupon(@Parameter(name = "优惠券id") @RequestBody AddCouponParam addCouponParam,
                             Long userId) {
        Long couponId = addCouponParam.getCouponId();
        String couponCode = addCouponParam.getCouponCode();
        if (couponId == null && couponCode == null){
            CMallException.fail("参数错误");
        }
        boolean saveResult = couponService.saveCouponUser(couponId, userId, couponCode);
        if (saveResult){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("领券失败");
    }
}
