package com.tsong.cmall.admin.goods.service.impl;

import com.tsong.cmall.admin.goods.mapper.AdminCarouselMapper;
import com.tsong.cmall.admin.goods.service.IAdminCarouselService;
import com.tsong.cmall.admin.goods.web.params.CarouselAddParam;
import com.tsong.cmall.admin.goods.web.params.CarouselEditParam;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.Carousel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/21 18:45
 */
@Service
public class AdminCarouselService implements IAdminCarouselService {
    @Autowired
    private AdminCarouselMapper adminCarouselMapper;

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageUtil) {
        List<Carousel> carousels = adminCarouselMapper.selectCarouselList(pageUtil);
        int total = adminCarouselMapper.getTotalCarousels(pageUtil);
        return new PageResult(carousels, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public String saveCarousel(CarouselAddParam carouselAddParam) {
        Carousel carousel = new Carousel();
        BeanUtil.copyProperties(carouselAddParam, carousel);
        if (adminCarouselMapper.insertSelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(CarouselEditParam carouselEditParam) {
        Carousel carousel = new Carousel();
        BeanUtil.copyProperties(carouselEditParam, carousel);
        Carousel temp = adminCarouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(new Date());
        if (adminCarouselMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return adminCarouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return adminCarouselMapper.deleteBatch(ids) > 0;
    }
}
