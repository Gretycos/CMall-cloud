package com.tsong.cmall.coupon.service;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.vo.coupon.MyCouponVO;
import com.tsong.cmall.entity.Coupon;
import com.tsong.cmall.entity.UserCouponRecord;

import java.math.BigDecimal;
import java.util.List;

public interface ICouponService {
    Coupon getCouponById(Long id);

    /**
     * @Description 通过领券记录获得优惠券
     * @Param [id]
     * @Return com.tsong.cmall.entity.Coupon
     */
    Coupon getByUserCouponId(Long id);

    /**
     * @Description 优惠券使用后更新状态
     * @Param [userCouponRecord]
     * @Return int
     */
    int updateUserCouponRecord(UserCouponRecord userCouponRecord);

    /**
     * @Description 新用户领券
     * @Param []
     * @Return java.util.List<com.tsong.cmall.entity.Coupon>
     */
    Boolean sendNewUserCoupons(Long userId);

    /**
     * @Description 用户可领优惠券
     * @Param [userId]
     * @Return java.util.List<com.tsong.cmall.controller.vo.CouponVO>
     */
    PageResult selectAvailableCoupons(Long userId, PageQueryUtil pageQueryUtil);

    /**
     * @Description 用户领取优惠券
     * @Param [couponId, userId]
     * @Return boolean
     */
    boolean saveCouponUser(Long couponId, Long userId, String couponCode);

    /**
     * @Description 查询领到的优惠券
     * @Param [pageQueryUtil]
     * @Return com.tsong.cmall.util.PageResult<com.tsong.cmall.controller.vo.CouponVO>
     */
    PageResult<MyCouponVO> selectMyCoupons(PageQueryUtil pageQueryUtil);

    List<MyCouponVO> selectAllMyAvailableCoupons(Long userId);

    /**
     * @Description 查询当前订单可用的优惠券
     * @Param [myShoppingCartItems, priceTotal, userId]
     * @Return java.util.List<com.tsong.cmall.controller.vo.CouponVO>
     */
    List<MyCouponVO> selectCouponsForOrderConfirm(List<Long> shoppingCartGoodsIdList,
                                                  BigDecimal priceTotal, Long userId);

    /**
     * @Description 删除优惠券
     * @Param [couponUserId]
     * @Return boolean
     */
    boolean deleteCouponUser(Long couponUserId);

    /**
     * @Description 释放未支付的优惠券
     * @Param [orderId]
     * @Return void
     */
    void releaseCoupon(Long orderId);

    /**
     * @Description 批量插入领券记录
     * @Param [userCouponRecordList]
     * @Return int
     */
    void insertUserCouponRecordBatch(List<UserCouponRecord> userCouponRecordList);

    /**
     * @Description 通过订单id查找优惠券
     * @Param [id]
     * @Return com.tsong.cmall.entity.Coupon
     */
    Coupon getByOrderId(Long id);

    void expireUserCoupons(List<Long> userCouponRecordIds);
}
