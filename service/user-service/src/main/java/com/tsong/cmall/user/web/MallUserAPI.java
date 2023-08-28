package com.tsong.cmall.user.web;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.NumberUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.user.service.IMallUserService;
import com.tsong.cmall.user.web.params.MallUserLoginParam;
import com.tsong.cmall.user.web.params.MallUserPasswordParam;
import com.tsong.cmall.user.web.params.MallUserRegisterParam;
import com.tsong.cmall.user.web.params.MallUserUpdateParam;
import com.tsong.cmall.user.web.vo.MallUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Tsong
 * @Date 2023/4/1 17:08
 */
@RestController
@Tag(name = "Mall User", description = "1-2.商城用户操作相关接口")
@RequestMapping("/api/user")
public class MallUserAPI {
    @Autowired
    private IMallUserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MallUserAPI.class);

    @PostMapping("/login")
    @Operation(summary = "登录接口", description = "返回token")
    public Result<String> login(@RequestBody @Valid MallUserLoginParam mallUserLoginParam) {
        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String loginResult = userService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());

        logger.info("login, loginName={},loginResult={}", mallUserLoginParam.getLoginName(), loginResult);

        //登录成功
        if (StringUtils.hasText(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }


    @PostMapping("/logout")
    @Operation(summary = "登出接口", description = "清除token")
    public Result<String> logout(@RequestParam Long userId) {
        Boolean logoutResult = userService.logout(userId);
        logger.info("logout, loginMallUser={}", userId);

        //登出成功
        if (logoutResult) {
            return ResultGenerator.genSuccessResult();
        }
        //登出失败
        return ResultGenerator.genFailResult("logout error");
    }


    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "")
    public Result mallUserRegister(@RequestBody @Valid MallUserRegisterParam mallUserRegisterParam) {
        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String registerResult = userService.register(mallUserRegisterParam.getLoginName(), mallUserRegisterParam.getPassword());

        logger.info("mallUserRegister, loginName={},loginResult={}", mallUserRegisterParam.getLoginName(), registerResult);

        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PutMapping("/info")
    @Operation(summary = "修改用户信息", description = "")
    public Result updateInfo(@RequestBody @Parameter(name = "用户信息") @Valid MallUserUpdateParam mallUserUpdateParam,
                             @RequestParam Long userId) {
        Boolean flag = userService.updateUserInfo(mallUserUpdateParam, userId);
        if (flag) {
            //返回成功
            return ResultGenerator.genSuccessResult("修改信息成功");
        } else {
            //返回失败
            return ResultGenerator.genFailResult("修改信息失败");
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改用户密码", description = "")
    public Result updatePassword(@RequestBody @Parameter(name = "用户密码") @Valid MallUserPasswordParam mallUserPasswordParam,
                                 @RequestParam Long userId) {
        Boolean flag = userService.updateUserPassword(mallUserPasswordParam, userId);
        if (flag) {
            //返回成功
            return ResultGenerator.genSuccessResult("修改密码成功");
        } else {
            //返回失败
            return ResultGenerator.genFailResult("原密码错误");
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "")
    public Result getUserDetail(@RequestParam Long userId) {
        return ResultGenerator.genSuccessResult(userService.getUserInfo(userId));
    }
}
