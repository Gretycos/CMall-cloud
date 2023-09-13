package com.tsong.cmall.admin.user.service;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;

public interface IAdminMallUserService {
    PageResult getMallUsersPage(PageQueryUtil pageUtil);

    /**
     * @Description 用户禁用与解除禁用(0-未锁定 1-已锁定)
     * @Param [ids, lockStatus]
     * @Return java.lang.Boolean
     */
    Boolean lockUsers(Long[] ids, int lockStatus);
}
