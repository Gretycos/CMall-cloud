package com.tsong.cmall.admin.goods.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.HomePageConfig;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminHomePageConfigMapper {
    int insertSelective(HomePageConfig row);

    HomePageConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(HomePageConfig row);

    HomePageConfig selectByTypeAndGoodsId(int configType, Long goodsId);

    List<HomePageConfig> selectHomePageConfigList(PageQueryUtil pageUtil);

    int getTotalHomePageConfigs(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);
}