package com.tsong.cmall.user.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class MallUserVO implements Serializable {
    @Schema(title = "用户id")
    private Long userId;

    @Schema(title = "用户昵称")
    private String nickName;

    @Schema(title = "用户登录名")
    private String loginName;

    @Schema(title = "用户简介")
    private String introduceSign;
}
