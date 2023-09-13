package com.tsong.cmall.admin.user.mapper;

import com.tsong.cmall.entity.AdminUser;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminUserMapper {
    int insert(AdminUser row);

    int insertSelective(AdminUser row);

    AdminUser selectByPrimaryKey(Long adminUserId);

    int updateByPrimaryKeySelective(AdminUser row);

    int updateByPrimaryKey(AdminUser row);

    /**
     * @Description 登录，相当于用账号密码查询用户是否存在
     * @Param [userName, password]
     * @Return com.tsong.cmall.entity.AdminUser
     */
    AdminUser login(String userName, String password);
}