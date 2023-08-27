package com.tsong.cmall.seckill.mapper;

import com.tsong.cmall.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface SeckillMapper {
    Seckill selectByPrimaryKey(Long seckillId);

    List<Seckill> getHomePageSeckillList();

    void killByProcedure(Map<String, Object> map);

    boolean addStock(Long seckillId);

    int putOffBatch(@Param("seckillIds") List<Long> seckillIds);
}