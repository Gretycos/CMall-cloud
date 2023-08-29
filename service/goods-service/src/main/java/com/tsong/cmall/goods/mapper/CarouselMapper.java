package com.tsong.cmall.goods.mapper;

import com.tsong.cmall.entity.Carousel;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface CarouselMapper {
    List<Carousel> selectCarouselsByNum(int number);
}