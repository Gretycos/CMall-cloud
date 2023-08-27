package com.tsong.cmall.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponRecord {
    private Long couponUserId;

    private Long userId;

    private Long couponId;

    private Byte useStatus;

    private Date usedTime;

    private Long orderId;

    private Date createTime;

    private Date updateTime;

    private Byte isDeleted;
}