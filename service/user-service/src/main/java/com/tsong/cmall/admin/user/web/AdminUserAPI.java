package com.tsong.cmall.admin.user.web;

import com.tsong.cmall.admin.user.service.IAdminUserService;
import com.tsong.cmall.admin.user.web.params.AdminLoginParam;
import com.tsong.cmall.admin.user.web.params.AdminNameUpdateParam;
import com.tsong.cmall.admin.user.web.params.AdminPasswordUpdateParam;
import com.tsong.cmall.admin.user.web.vo.LoginVO;
import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.AdminUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Tsong
 * @Date 2023/4/3 17:30
 */
@RestController
@Tag(name = "Admin User", description = "2-0.后台管理系统管理员模块接口")
@RequestMapping("/admin/user")
public class AdminUserAPI {
    @Autowired
    private IAdminUserService adminUserService;

//    private static final Logger logger = LoggerFactory.getLogger(AdminUserAPI.class);

    @PostMapping(value = "/login")
    public Result login(@RequestBody @Valid AdminLoginParam adminLoginParam) {
        LoginVO loginResult = adminUserService.login(adminLoginParam.getUserName(), adminLoginParam.getPasswordMd5());
        //登录成功
        if (loginResult != null) {
            return ResultGenerator.genSuccessResult(loginResult);
        }
        //登录失败
        return ResultGenerator.genFailResult("登录失败");
    }

    @GetMapping(value = "/profile")
    public Result profile(Long adminId) {
        AdminUser adminUserFromDB = adminUserService.getUserDetailById(adminId);
        if (adminUserFromDB != null) {
            adminUserFromDB.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUserFromDB);
            return result;
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    @PutMapping(value = "/password")
    public Result passwordUpdate(@RequestBody @Valid AdminPasswordUpdateParam adminPasswordParam, Long adminId) {
        if (adminUserService.updatePassword(adminId, adminPasswordParam.getOriginalPassword(), adminPasswordParam.getNewPassword())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.PASSWORD_INCORRECT.getResult());
        }
    }

    @PutMapping(value = "/name")
    public Result nameUpdate(@RequestBody @Valid AdminNameUpdateParam adminNameParam, Long adminId) {
        if (adminUserService.updateName(adminId, adminNameParam.getLoginUserName(), adminNameParam.getNickName())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    @DeleteMapping(value = "/logout")
    public Result logout(Long adminId) {
        adminUserService.logout(adminId);
        return ResultGenerator.genSuccessResult();
    }
}
