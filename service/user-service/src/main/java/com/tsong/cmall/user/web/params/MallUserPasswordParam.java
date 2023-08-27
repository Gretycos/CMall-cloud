package com.tsong.cmall.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/1 17:39
 */
@Data
public class MallUserPasswordParam implements Serializable {
    @Schema(title = "原始密码")
    @NotEmpty(message = "密码不能为空")
    private String originalPassword;

    @Schema(title = "新密码")
    @NotEmpty(message = "密码不能为空")
    private String newPassword;
}
