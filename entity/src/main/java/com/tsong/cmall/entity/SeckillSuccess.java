package com.tsong.cmall.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillSuccess {
    private Long secId;

    private Long seckillId;

    private Long userId;

    private Byte state;

    private Date createTime;
}