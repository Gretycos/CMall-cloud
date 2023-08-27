package com.tsong.cmall.user.mapper;

import com.tsong.cmall.entity.MallUser;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface MallUserMapper {
    int insertSelective(MallUser row);

    MallUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(MallUser row);

    MallUser selectByLoginName(String loginName);

    MallUser selectByLoginNameAndPasswd(String loginName, String password);
}