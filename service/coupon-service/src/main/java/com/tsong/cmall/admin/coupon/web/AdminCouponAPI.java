package com.tsong.cmall.admin.coupon.web;

import com.tsong.cmall.admin.coupon.service.IAdminCouponService;
import com.tsong.cmall.admin.coupon.web.params.CouponAddParam;
import com.tsong.cmall.admin.coupon.web.params.CouponEditParam;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/4/11 13:28
 */
@RestController
@Tag(name = "Admin Coupon", description = "2-8.后台管理优惠券模块接口")
@RequestMapping("/admin/coupon")
public class AdminCouponAPI {

//    private static final Logger logger = LoggerFactory.getLogger(AdminCouponAPI.class);

    @Autowired
    private IAdminCouponService adminCouponService;

    @GetMapping("/")
    @Operation(summary = "优惠券列表", description = "")
    public Result couponList(@Parameter(name = "页码") @RequestParam(required = false) Integer pageNumber,
                             @Parameter(name = "每页条数") @RequestParam(required = false) Integer pageSize,
                             Long adminId) {
        Map<String, Object> params = new HashMap<>(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 10 || pageSize > 100){
            pageSize = 10;
        }
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(adminCouponService.getCouponPage(pageUtil));
    }

    @PostMapping("/")
    @Operation(summary = "新增优惠券", description = "")
    public Result saveCoupon(@Parameter(name = "优惠券新增参数") @RequestBody @Valid CouponAddParam couponAddParam,
                             Long adminId) {
        boolean result = adminCouponService.saveCoupon(couponAddParam);
        if (!result){
            return ResultGenerator.genFailResult("新增优惠券失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping("/")
    @Operation(summary = "修改优惠券", description = "")
    public Result updateCoupon(@Parameter(name = "优惠券修改参数") @RequestBody @Valid CouponEditParam couponEditParam,
                               Long adminId){
        boolean result = adminCouponService.updateCoupon(couponEditParam);
        if (!result){
            return ResultGenerator.genFailResult("更新优惠券失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @Operation(summary = "优惠券详情", description = "")
    public Result couponInfo(@Parameter(name = "优惠券id") @PathVariable("id") Long id,
                       Long adminId) {
        Coupon coupon = adminCouponService.getCouponById(id);
        return ResultGenerator.genSuccessResult(coupon);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除优惠券", description = "")
    public Result deleteCoupon(@Parameter(name = "优惠券id") @PathVariable Long id,
                         Long adminId) {
        boolean result = adminCouponService.deleteCouponById(id);
        if (!result){
            return ResultGenerator.genFailResult("删除优惠券失败");
        }
        return ResultGenerator.genSuccessResult();
    }
}
