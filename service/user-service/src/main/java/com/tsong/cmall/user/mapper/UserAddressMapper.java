package com.tsong.cmall.user.mapper;

import com.tsong.cmall.entity.UserAddress;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/27/05:14
 */
public interface UserAddressMapper {
    int deleteByPrimaryKey(Long addressId);

    int insertSelective(UserAddress row);

    UserAddress selectByPrimaryKey(Long addressId);

    int updateByPrimaryKeySelective(UserAddress row);

    UserAddress getMyDefaultAddress(Long userId);

    List<UserAddress> getMyAddressList(Long userId);
}