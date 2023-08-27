package com.tsong.cmall.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/1 17:15
 */
@Data
public class MallUserLoginParam implements Serializable {
    @Schema(title = "登录名")
    @NotEmpty(message = "登录名不能为空")
    @Length(max = 16,message = "登录名过长")
    private String loginName;

    @Schema(title = "用户密码(需要MD5加密)")
    @NotEmpty(message = "密码不能为空")
    @Length(max = 32,message = "密码过长")
    private String passwordMd5;
}
