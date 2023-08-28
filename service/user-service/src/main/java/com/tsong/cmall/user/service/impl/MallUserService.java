package com.tsong.cmall.user.service.impl;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.*;
import com.tsong.cmall.entity.MallUser;
import com.tsong.cmall.entity.UserToken;
import com.tsong.cmall.user.service.IMallUserService;
import com.tsong.cmall.user.mapper.MallUserMapper;
import com.tsong.cmall.user.mapper.UserTokenMapper;
import com.tsong.cmall.user.redis.RedisCache;
import com.tsong.cmall.user.web.params.MallUserPasswordParam;
import com.tsong.cmall.user.web.params.MallUserUpdateParam;
import com.tsong.cmall.user.web.vo.MallUserVO;
import com.tsong.feign.clients.coupon.CouponClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.tsong.cmall.common.enums.ServiceResultEnum.RPC_ERROR;

/**
 * @Author Tsong
 * @Date 2023/3/24 14:57
 */
@Service
public class MallUserService implements IMallUserService {
    @Autowired
    private MallUserMapper mallUserMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private CouponClient couponClient;

    @Autowired
    private RedisCache redisCache;

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        String passwordMD5 = MD5Util.MD5Encode(password, Constants.UTF_ENCODING);

        MallUser registerUser = MallUser.builder()
                .loginName(loginName)
                .nickName(loginName)
                .passwordMd5(passwordMD5)
                .build();
        if (mallUserMapper.insertSelective(registerUser) <= 0) {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        // 添加注册赠券
        // 添加领券记录
        Result sendCouponsResult = couponClient.sendNewUserCoupons(registerUser.getUserId());
        if (sendCouponsResult.getResultCode() != 200){
            CMallException.fail(RPC_ERROR.getResult() + sendCouponsResult.getMessage());
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            // 新token
            String token = genNewToken(System.currentTimeMillis() + "", user.getUserId());
            // 当前时间
            Date now = new Date();
            // 过期时间
            Date expireTime = new Date(now.getTime() + Constants.TOKEN_EXPIRED_TIME); // 过期时间 48 小时

            UserToken userToken = userTokenMapper.selectByPrimaryKey(user.getUserId());
            if (userToken == null) {
                // 用户没登录过
                userToken = UserToken.builder()
                        .userId(user.getUserId())
                        .token(token)
                        .updateTime(now)
                        .expireTime(expireTime)
                        .build();
                //新增一条token数据
                if (userTokenMapper.insertSelective(userToken) > 0) {
                    redisCache.setCacheObject(Constants.MALL_USER_TOKEN_KEY + token, user.getUserId(),
                            Constants.TOKEN_EXPIRED_TIME, TimeUnit.MILLISECONDS);
                    //新增成功后返回
                    return token;
                }
            } else {
                // 单点登录
                // 删除
                redisCache.deleteObject(Constants.MALL_USER_TOKEN_KEY + userToken.getToken());
                // 用户登录过，修改token
                userToken.setToken(token);
                userToken.setUpdateTime(now);
                userToken.setExpireTime(expireTime);
                redisCache.setCacheObject(Constants.MALL_USER_TOKEN_KEY + token, user,
                        Constants.TOKEN_EXPIRED_TIME, TimeUnit.MILLISECONDS);
                //更新
                if (userTokenMapper.updateByPrimaryKeySelective(userToken) > 0) {
                    //修改成功后返回
                    return token;
                }
            }
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public MallUserVO getUserInfo(Long userId) {
        MallUserVO mallUserVO = new MallUserVO();
        MallUser mallUser = mallUserMapper.selectByPrimaryKey(userId);
        BeanUtil.copyProperties(mallUser, mallUserVO);
        return mallUserVO;
    }

    /**
     * @Description 获取token值
     * @Param [timeStr, userId]
     * @Return java.lang.String
     */
    private String genNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return TokenUtil.genToken(src);
    }

    @Override
    public Boolean updateUserInfo(MallUserUpdateParam mallUserUpdateParam, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            CMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUserUpdateParam.getNickName());
        user.setIntroduceSign(mallUserUpdateParam.getIntroduceSign());
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0){
            String token = userTokenMapper.selectByPrimaryKey(userId).getToken();
            redisCache.deleteObject(Constants.MALL_USER_TOKEN_KEY + token);
            return true;
        }
        return false;
    }

    @Override
//    @Transactional
    public Boolean updateUserPassword(MallUserPasswordParam mallUserPasswordParam, Long loginUserId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(loginUserId);
        if (user == null){
            CMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        String originalPassword = mallUserPasswordParam.getOriginalPassword();
        String newPassword = mallUserPasswordParam.getNewPassword();

        if (originalPassword.equals(user.getPasswordMd5())){
            user.setPasswordMd5(newPassword);
            if (mallUserMapper.updateByPrimaryKeySelective(user) <= 0){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            if (!logout(loginUserId)){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            return true;
        }
        return false;
    }

    @Override
    public Boolean logout(Long userId) {
        String token = userTokenMapper.selectByPrimaryKey(userId).getToken();
        redisCache.deleteObject(Constants.MALL_USER_TOKEN_KEY + token);
        return userTokenMapper.deleteByPrimaryKey(userId) > 0;
    }
}
