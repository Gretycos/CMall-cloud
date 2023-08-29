package com.tsong.cmall.goods.mapper;

import com.tsong.cmall.entity.HomePageConfig;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface HomePageConfigMapper {
    List<HomePageConfig> selectHomePageConfigsByTypeAndNum(int configType, int number);
}