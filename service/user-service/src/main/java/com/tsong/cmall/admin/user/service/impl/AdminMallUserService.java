package com.tsong.cmall.admin.user.service.impl;

import com.tsong.cmall.admin.user.mapper.AdminMallUserMapper;
import com.tsong.cmall.admin.user.service.IAdminMallUserService;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.MallUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/24 14:57
 */
@Service
public class AdminMallUserService implements IAdminMallUserService {
    @Autowired
    private AdminMallUserMapper adminMallUserMapper;

    @Override
    public PageResult getMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = adminMallUserMapper.selectMallUserList(pageUtil);
        int total = adminMallUserMapper.getTotalMallUsers(pageUtil);
        return new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public Boolean lockUsers(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return adminMallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
