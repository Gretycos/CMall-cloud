package com.tsong.cmall.admin.user.service.impl;

import com.tsong.cmall.admin.user.mapper.AdminUserMapper;
import com.tsong.cmall.admin.user.mapper.AdminUserTokenMapper;
import com.tsong.cmall.admin.user.service.IAdminUserService;
import com.tsong.cmall.admin.user.web.vo.LoginVO;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.NumberUtil;
import com.tsong.cmall.common.util.TokenUtil;
import com.tsong.cmall.entity.AdminUser;
import com.tsong.cmall.entity.AdminUserToken;
import com.tsong.cmall.user.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.tsong.cmall.common.constants.Constants.ADMIN_USER_ID_TOKEN_KEY;
import static com.tsong.cmall.common.constants.Constants.TOKEN_EXPIRED_TIME;

@Service
public class AdminUserService implements IAdminUserService {
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private AdminUserTokenMapper adminUserTokenMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public LoginVO login(String userName, String password) {
        AdminUser loginAdminUser = adminUserMapper.login(userName, password);
        if (loginAdminUser != null) {
            //登录后即执行修改token的操作
            String token = genNewToken(System.currentTimeMillis() + "", loginAdminUser.getAdminUserId());
            AdminUserToken adminUserToken = adminUserTokenMapper.selectByPrimaryKey(loginAdminUser.getAdminUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000); // 过期时间 48 小时
            if (adminUserToken == null) {
                adminUserToken = AdminUserToken.builder()
                        .adminUserId(loginAdminUser.getAdminUserId())
                        .token(token)
                        .updateTime(now)
                        .expireTime(expireTime)
                        .build();
                //新增一条token数据
                if (adminUserTokenMapper.insertSelective(adminUserToken) > 0) {
                    redisCache.setCacheObject(ADMIN_USER_ID_TOKEN_KEY + loginAdminUser.getAdminUserId(), token,
                            TOKEN_EXPIRED_TIME, TimeUnit.MILLISECONDS);
                    LoginVO loginVO = new LoginVO();
                    loginVO.setAdminId(loginAdminUser.getAdminUserId());
                    loginVO.setToken(token);
                    //新增成功后返回
                    return loginVO;
                }
            } else {
                redisCache.deleteObject(ADMIN_USER_ID_TOKEN_KEY + loginAdminUser.getAdminUserId());
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);
                //更新
                if (adminUserTokenMapper.updateByPrimaryKeySelective(adminUserToken) > 0) {
                    redisCache.setCacheObject(ADMIN_USER_ID_TOKEN_KEY + loginAdminUser.getAdminUserId(), token,
                            TOKEN_EXPIRED_TIME, TimeUnit.MILLISECONDS);
                    LoginVO loginVO = new LoginVO();
                    loginVO.setAdminId(loginAdminUser.getAdminUserId());
                    loginVO.setToken(token);
                    //修改成功后返回
                    return loginVO;
                }
            }

        }
        return null;
    }

    /**
     * @Description 获取token值
     * @Param [timeStr, userId]
     * @Return java.lang.String
     */
    private String genNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(6);
        return TokenUtil.genToken(src);
    }

    @Override
    public AdminUser getUserDetailById(Long loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    @Transactional
    public Boolean updatePassword(Long loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        // 当前用户非空才可以进行更改
        if (adminUser == null){
            CMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        // 比较原密码是否正确
        if (originalPassword.equals(adminUser.getLoginPassword())) {
            // 设置新密码并修改
            adminUser.setLoginPassword(newPassword);
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser) <= 0){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            // 修改成功则清空token
            if (!logout(loginUserId)){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateName(Long loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            //修改成功则返回true
            return adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0;
        }
        return false;
    }

    @Override
    public Boolean logout(Long adminUserId) {
        Boolean res = adminUserTokenMapper.deleteByPrimaryKey(adminUserId) > 0;
        if (res) {
            redisCache.deleteObject(ADMIN_USER_ID_TOKEN_KEY + adminUserId);
        }
        return res;
    }
}
