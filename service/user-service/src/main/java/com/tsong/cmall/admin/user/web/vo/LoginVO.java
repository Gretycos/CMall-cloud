package com.tsong.cmall.admin.user.web.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/9/1 01:19
 */
@Data
public class LoginVO implements Serializable {
    private Long adminId;
    private String token;
}
