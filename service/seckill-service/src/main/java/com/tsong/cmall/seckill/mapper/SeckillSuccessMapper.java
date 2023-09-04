package com.tsong.cmall.seckill.mapper;

import com.tsong.cmall.entity.SeckillSuccess;
import com.tsong.cmall.seckill.mapper.dto.SeckillSuccessDTO;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface SeckillSuccessMapper {
    int deleteByPrimaryKey(Long secId);

    SeckillSuccess getSeckillSuccessByUserIdAndSeckillId(Long userId, Long seckillId);

    int insertSuccessRecord(SeckillSuccessDTO seckillSuccessDTO);
}