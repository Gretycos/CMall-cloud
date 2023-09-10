package com.tsong.cmall.goods.service.impl;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.entity.HomePageConfig;
import com.tsong.cmall.goods.enums.CategoryLevelEnum;
import com.tsong.cmall.goods.enums.HomePageConfigTypeEnum;
import com.tsong.cmall.goods.mapper.GoodsCategoryMapper;
import com.tsong.cmall.goods.mapper.GoodsInfoMapper;
import com.tsong.cmall.goods.mapper.HomePageConfigMapper;
import com.tsong.cmall.goods.service.ICarouselService;
import com.tsong.cmall.goods.service.IHomePageConfigService;
import com.tsong.cmall.goods.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @Author Tsong
 * @Date 2023/3/24 13:44
 */
@Service
public class HomePageConfigConfigService implements IHomePageConfigService {
    @Autowired
    private HomePageConfigMapper homePageConfigMapper;

    @Autowired
    private GoodsCategoryMapper categoryMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private ICarouselService carouselService;


    @Override
    public HomePageInfoVO getHomePageInfo() {
        HomePageInfoVO homePageInfoVO = new HomePageInfoVO();
        List<HomePageCarouselVO> carouselList = carouselService
                .getCarouselsForHomePage(Constants.HOME_PAGE_CAROUSEL_NUMBER);
        List<HomePageConfigGoodsVO> hotGoodsList = getHomePageGoodsListByTypeAndNum(
                HomePageConfigTypeEnum.HOME_PAGE_GOODS_HOT.getType(),
                Constants.HOME_PAGE_GOODS_HOT_NUMBER);
        List<HomePageConfigGoodsVO> newGoodsList = getHomePageGoodsListByTypeAndNum(
                HomePageConfigTypeEnum.HOME_PAGE_GOODS_NEW.getType(),
                Constants.HOME_PAGE_GOODS_NEW_NUMBER);
        List<HomePageConfigGoodsVO> recommendGoodsList = getHomePageGoodsListByTypeAndNum(
                HomePageConfigTypeEnum.HOME_PAGE_GOODS_RECOMMENDED.getType(),
                Constants.HOME_PAGE_GOODS_RECOMMENDED_NUMBER);
        homePageInfoVO.setCarouselList(carouselList);
        homePageInfoVO.setHotGoodsList(hotGoodsList);
        homePageInfoVO.setNewGoodsList(newGoodsList);
        homePageInfoVO.setRecommendGoodsList(recommendGoodsList);
        return homePageInfoVO;
    }

    @Override
    public List<HomePageCategoryVO> getCategoriesForHomePage() {
        List<HomePageCategoryVO> homePageCategoryVOList = new ArrayList<>();
        // 获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.HOME_PAGE_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            // 一级分类id
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream()
                    .map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            // 获取二级分类的数据
            List<GoodsCategory> secondLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                    firstLevelCategoryIds, CategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                // 二级分类id
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream()
                        .map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                // 获取三级分类的数据
                List<GoodsCategory> thirdLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                        secondLevelCategoryIds, CategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    // 根据 parentId 将 thirdLevelCategories 分组
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream()
                            .collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOList = new ArrayList<>();
                    // 处理二级分类
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        // 根据二级分类的id取出 thirdLevelCategoryVOMap 分组中的三级级分类list
                        List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                        // 如果该二级分类下有数据则放入 secondLevelCategoryVOList 对象中
                        if (tempGoodsCategories != null) {
                            secondLevelCategoryVO.setThirdLevelCategoryVOList((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOList.add(secondLevelCategoryVO);
                        }
                    }
                    // 处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOList)) {
                        // 根据 parentId 将 secondLevelCategories 分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOList.stream()
                                .collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstCategory : firstLevelCategories) {
                            HomePageCategoryVO homePageCategoryVO = new HomePageCategoryVO();
                            BeanUtil.copyProperties(firstCategory, homePageCategoryVO);
                            // 根据一级分类的id取出 secondLevelCategoryVOMap 分组中的二级级分类list
                            List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                            // 如果该一级分类下有数据则放入 homePageCategoryVOList 对象中
                            if (tempGoodsCategories != null) {
                                homePageCategoryVO.setSecondLevelCategoryVOList(tempGoodsCategories);
                                homePageCategoryVOList.add(homePageCategoryVO);
                            }
                        }
                    }
                }
            }
            return homePageCategoryVOList;
        } else {
            return null;
        }
    }

    private List<HomePageConfigGoodsVO> getHomePageGoodsListByTypeAndNum(int configType, int number){
        List<HomePageConfigGoodsVO> homePageConfigGoodsVOList = new ArrayList<>(number);
        List<HomePageConfig> homePageConfigList = homePageConfigMapper.selectHomePageConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(homePageConfigList)) {
            //取出所有的goodsId
            List<Long> goodsIds = homePageConfigList.stream()
                    .map(HomePageConfig::getGoodsId).collect(Collectors.toList());
            List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByPrimaryKeys(goodsIds);
            homePageConfigGoodsVOList = BeanUtil.copyList(goodsInfoList, HomePageConfigGoodsVO.class);
            for (HomePageConfigGoodsVO homePageConfigGoodsVO : homePageConfigGoodsVOList) {
                String goodsName = homePageConfigGoodsVO.getGoodsName();
                String goodsIntro = homePageConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    homePageConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    homePageConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return homePageConfigGoodsVOList;
    }
}
