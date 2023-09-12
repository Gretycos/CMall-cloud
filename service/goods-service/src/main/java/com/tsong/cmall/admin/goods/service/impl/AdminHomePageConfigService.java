package com.tsong.cmall.admin.goods.service.impl;

import com.tsong.cmall.admin.goods.mapper.AdminGoodsMapper;
import com.tsong.cmall.admin.goods.mapper.AdminHomePageConfigMapper;
import com.tsong.cmall.admin.goods.service.IAdminHomePageConfigService;

import com.tsong.cmall.admin.goods.web.params.HomePageConfigAddParam;
import com.tsong.cmall.admin.goods.web.params.HomePageConfigEditParam;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.entity.HomePageConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/24 13:44
 */
@Service
public class AdminHomePageConfigService implements IAdminHomePageConfigService {
    @Autowired
    private AdminHomePageConfigMapper adminHomePageConfigMapper;

    @Autowired
    private AdminGoodsMapper adminGoodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<HomePageConfig> homePageConfigList = adminHomePageConfigMapper.selectHomePageConfigList(pageUtil);
        int total = adminHomePageConfigMapper.getTotalHomePageConfigs(pageUtil);
        return new PageResult(homePageConfigList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public String saveHomePageConfig(HomePageConfigAddParam homePageConfigAddParam) {
        HomePageConfig homePageConfig = new HomePageConfig();
        BeanUtil.copyProperties(homePageConfigAddParam, homePageConfig);
        GoodsInfo goods = adminGoodsMapper.selectByPrimaryKey(homePageConfig.getGoodsId());
        if (goods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        if (adminHomePageConfigMapper.selectByTypeAndGoodsId(
                homePageConfig.getConfigType(), homePageConfig.getGoodsId()) != null) {
            return ServiceResultEnum.SAME_HOME_PAGE_CONFIG_EXIST.getResult();
        }
        if (adminHomePageConfigMapper.insertSelective(homePageConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateHomePageConfig(HomePageConfigEditParam homePageConfigEditParam) {
        HomePageConfig homePageConfig = new HomePageConfig();
        BeanUtil.copyProperties(homePageConfigEditParam, homePageConfig);
        GoodsInfo goods = adminGoodsMapper.selectByPrimaryKey(homePageConfig.getGoodsId());
        if (goods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        HomePageConfig temp = adminHomePageConfigMapper.selectByPrimaryKey(homePageConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        HomePageConfig temp2 = adminHomePageConfigMapper.selectByTypeAndGoodsId(
                homePageConfig.getConfigType(), homePageConfig.getGoodsId());
        if (temp2 != null && !temp2.getConfigId().equals(homePageConfig.getConfigId())) {
            //goodsId相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_HOME_PAGE_CONFIG_EXIST.getResult();
        }
        homePageConfig.setUpdateTime(new Date());
        if (adminHomePageConfigMapper.updateByPrimaryKeySelective(homePageConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public HomePageConfig getHomePageConfigById(Long id) {
        return adminHomePageConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return adminHomePageConfigMapper.deleteBatch(ids) > 0;
    }
}
