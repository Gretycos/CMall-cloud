package com.tsong.cmall.admin.goods.service;

import com.tsong.cmall.admin.goods.web.params.CarouselAddParam;
import com.tsong.cmall.admin.goods.web.params.CarouselEditParam;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.Carousel;

import java.util.List;

public interface IAdminCarouselService {
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(CarouselAddParam carouselAddParam);

    String updateCarousel(CarouselEditParam carouselEditParam);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Long[] ids);
}
