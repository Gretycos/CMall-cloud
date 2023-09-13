package com.tsong.cmall.admin.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/3 17:38
 */
@Data
public class AdminLoginParam implements Serializable {
    @Schema(title = "登录名")
    @NotEmpty(message = "登录名不能为空")
    private String userName;

    @Schema(title = "用户密码(需要MD5加密)")
    @NotEmpty(message = "密码不能为空")
    private String passwordMd5;
}
