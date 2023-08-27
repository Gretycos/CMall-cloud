package com.tsong.cmall.user.service;

import com.tsong.cmall.user.web.params.MallUserPasswordParam;
import com.tsong.cmall.user.web.params.MallUserUpdateParam;

public interface IMallUserService {
    /**
     * @Description 用户注册
     * @Param [loginName, password]
     * @Return java.lang.String
     */
    String register(String loginName, String password);

    /**
     * @Description 登录
     * @Param [loginName, passwordMD5, httpSession]
     * @Return java.lang.String
     */
    String login(String loginName, String passwordMD5);

    /**
     * @Description 用户信息修改并返回最新的用户信息
     * @Param [userId, newNickName, newIntroduceSign]
     * @Return java.lang.Boolean
     */
    Boolean updateUserInfo(MallUserUpdateParam mallUserUpdateParam, Long userId);

    /**
     * @Description 用户修改密码
     * @Param [loginUserId, originalPassword, newPassword]
     * @Return java.lang.Boolean
     */
    Boolean updateUserPassword(MallUserPasswordParam mallUserPasswordParam, Long loginUserId);

    /**
     * @Description 登出接口
     * @Param [userId]
     * @Return java.lang.Boolean
     */
    Boolean logout(Long userId);

}
