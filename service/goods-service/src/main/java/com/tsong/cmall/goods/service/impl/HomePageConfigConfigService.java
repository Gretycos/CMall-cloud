package com.tsong.cmall.goods.service.impl;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.entity.HomePageConfig;
import com.tsong.cmall.goods.enums.HomePageConfigTypeEnum;
import com.tsong.cmall.goods.mapper.GoodsInfoMapper;
import com.tsong.cmall.goods.mapper.HomePageConfigMapper;
import com.tsong.cmall.goods.service.ICarouselService;
import com.tsong.cmall.goods.service.IHomePageConfigService;
import com.tsong.cmall.goods.web.vo.HomePageCarouselVO;
import com.tsong.cmall.goods.web.vo.HomePageConfigGoodsVO;
import com.tsong.cmall.goods.web.vo.HomePageInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Tsong
 * @Date 2023/3/24 13:44
 */
@Service
public class HomePageConfigConfigService implements IHomePageConfigService {
    @Autowired
    private HomePageConfigMapper homePageConfigMapper;

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
