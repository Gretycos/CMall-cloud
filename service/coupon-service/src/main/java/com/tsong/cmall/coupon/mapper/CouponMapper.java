package com.tsong.cmall.coupon.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.Coupon;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface CouponMapper {
    Coupon selectByPrimaryKey(Long couponId);

    Coupon selectByCode(String code);

    List<Coupon> selectAvailableCoupon(PageQueryUtil pageUtil);

    int getTotalAvailableCoupon(PageQueryUtil pageUtil);

    int reduceCouponTotal(Long couponId);

    List<Coupon> selectByIds(List<Long> couponIds);

    List<Coupon> selectAvailableGivenCoupon();
}