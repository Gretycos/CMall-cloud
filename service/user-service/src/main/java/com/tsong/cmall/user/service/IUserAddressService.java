package com.tsong.cmall.user.service;

import com.tsong.cmall.entity.UserAddress;
import com.tsong.cmall.user.web.vo.UserAddressVO;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/29 22:51
 */
public interface IUserAddressService {
    /**
     * @Description 获取我的收货地址
     * @Param [userId]
     * @Return java.util.List<UserAddressVO>
     */
    List<UserAddressVO> getMyAddresses(Long userId);

    /**
     * @Description 保存收货地址
     * @Param [userAddress]
     * @Return java.lang.Boolean
     */
    Boolean saveUserAddress(UserAddress userAddress);

    /**
     * @Description 修改收货地址
     * @Param [userAddress]
     * @Return java.lang.Boolean
     */
    Boolean updateUserAddress(UserAddress userAddress);

    /**
     * @Description 获取收货地址详情
     * @Param [addressId]
     * @Return com.tsong.cmall.entity.UserAddress
     */
    UserAddress getUserAddressById(Long addressId);

    /**
     * @Description 获取我的默认收货地址
     * @Param [userId]
     * @Return com.tsong.cmall.entity.UserAddress
     */
    UserAddress getMyDefaultAddressByUserId(Long userId);

    /**
     * @Description 删除收货地址
     * @Param [addressId]
     * @Return java.lang.Boolean
     */
    Boolean deleteById(Long addressId);
}
