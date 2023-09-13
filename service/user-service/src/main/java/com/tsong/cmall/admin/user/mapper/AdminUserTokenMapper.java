package com.tsong.cmall.admin.user.mapper;

import com.tsong.cmall.entity.AdminUserToken;

/**
 * @Author: Tsong
 * @date: 2023/03/27/05:14
 */
public interface AdminUserTokenMapper {
    int deleteByPrimaryKey(Long adminUserId);

    int insert(AdminUserToken row);

    int insertSelective(AdminUserToken row);

    AdminUserToken selectByPrimaryKey(Long adminUserId);

    int updateByPrimaryKeySelective(AdminUserToken row);

    int updateByPrimaryKey(AdminUserToken row);

    AdminUserToken selectByToken(String token);
}