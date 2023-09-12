package com.tsong.cmall.admin.coupon.service;

import com.tsong.cmall.admin.coupon.web.params.CouponAddParam;
import com.tsong.cmall.admin.coupon.web.params.CouponEditParam;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.Coupon;


public interface IAdminCouponService {
    PageResult getCouponPage(PageQueryUtil pageUtil);

    boolean saveCoupon(CouponAddParam couponAddParam);

    boolean updateCoupon(CouponEditParam couponEditParam);

    Coupon getCouponById(Long id);

    boolean deleteCouponById(Long id);
}
