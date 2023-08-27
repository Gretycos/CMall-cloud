package com.tsong.cmall.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Tsong
 * @date: 2023/03/27/05:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddress {
    private Long orderId;

    private String userName;

    private String userPhone;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;

    @Override
    public String toString() {
        return userName + '，'
                + userPhone + '，'
                + provinceName + ' '
                + cityName + ' '
                + regionName + ' '
                + detailAddress;
    }
}