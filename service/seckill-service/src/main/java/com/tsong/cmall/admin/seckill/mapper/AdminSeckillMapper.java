package com.tsong.cmall.admin.seckill.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminSeckillMapper {
    int deleteByPrimaryKey(Long seckillId);

    int insertSelective(Seckill row);

    Seckill selectByPrimaryKey(Long seckillId);

    int updateByPrimaryKeySelective(Seckill row);

    List<Seckill> selectSeckillList(PageQueryUtil pageUtil);

    int getTotalSeckills(PageQueryUtil pageUtil);

    int putOffBatch(@Param("seckillIds") List<Long> seckillIds);
}