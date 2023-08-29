package com.tsong.cmall.goods.service.impl;

import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.entity.Carousel;
import com.tsong.cmall.goods.mapper.CarouselMapper;
import com.tsong.cmall.goods.service.ICarouselService;
import com.tsong.cmall.goods.web.vo.HomePageCarouselVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/21 18:45
 */
@Service
public class CarouselService implements ICarouselService {
    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<HomePageCarouselVO> getCarouselsForHomePage(int number) {
        List<HomePageCarouselVO> homePageCarouselVOList = new ArrayList<>(number);
        List<Carousel> carouselList = carouselMapper.selectCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carouselList)) {
            homePageCarouselVOList = BeanUtil.copyList(carouselList, HomePageCarouselVO.class);
        }
        return homePageCarouselVOList;
    }
}
