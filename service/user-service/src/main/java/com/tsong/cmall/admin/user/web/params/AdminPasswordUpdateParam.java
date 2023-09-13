package com.tsong.cmall.admin.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/3 17:42
 */
@Data
public class AdminPasswordUpdateParam implements Serializable {
    @NotEmpty(message = "originalPassword不能为空")
    @Schema(title = "初始密码，md5加密")
    private String originalPassword;

    @NotEmpty(message = "newPassword不能为空")
    @Schema(title = "新密码，md5加密")
    private String newPassword;
}
