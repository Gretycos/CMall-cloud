package com.tsong.cmall.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Tsong
 * @date: 2023/03/27/05:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserToken {
    private Long adminUserId;

    private String token;

    private Date updateTime;

    private Date expireTime;
}