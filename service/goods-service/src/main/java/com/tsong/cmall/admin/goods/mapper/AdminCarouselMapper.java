package com.tsong.cmall.admin.goods.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.Carousel;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminCarouselMapper {
    int insertSelective(Carousel row);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel row);

    List<Carousel> selectCarouselList(PageQueryUtil pageUtil);

    int getTotalCarousels(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);
}