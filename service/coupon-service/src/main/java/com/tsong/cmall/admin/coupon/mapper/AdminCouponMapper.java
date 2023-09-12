package com.tsong.cmall.admin.coupon.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.Coupon;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminCouponMapper {
    int deleteByPrimaryKey(Long couponId);

    int insertSelective(Coupon row);

    Coupon selectByPrimaryKey(Long couponId);

    int updateByPrimaryKeySelective(Coupon row);

    List<Coupon> selectCouponList(PageQueryUtil pageUtil);

    int getTotalCoupons(PageQueryUtil pageUtil);

}