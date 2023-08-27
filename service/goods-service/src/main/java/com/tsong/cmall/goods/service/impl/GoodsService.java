package com.tsong.cmall.goods.service.impl;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.goods.mapper.GoodsCategoryMapper;
import com.tsong.cmall.goods.mapper.GoodsInfoMapper;
import com.tsong.cmall.goods.service.IGoodsService;
import com.tsong.cmall.goods.web.vo.SearchPageGoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/23 23:20
 */
@Service
public class GoodsService implements IGoodsService {
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;


    @Override
    public GoodsInfo getGoodsInfoById(Long id) {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(id);
        if (goodsInfo == null) {
            CMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return goodsInfo;
    }

    @Override
    public PageResult searchGoodsInfo(PageQueryUtil pageUtil) {
        List<GoodsInfo> goodsList = goodsInfoMapper.selectGoodsListBySearch(pageUtil);
        int total = goodsList.size();
        List<SearchPageGoodsVO> searchPageGoodsVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            searchPageGoodsVOList = BeanUtil.copyList(goodsList, SearchPageGoodsVO.class);
            for (SearchPageGoodsVO searchPageGoodsVO : searchPageGoodsVOList) {
                String goodsName = searchPageGoodsVO.getGoodsName();
                String goodsIntro = searchPageGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    searchPageGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    searchPageGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return new PageResult(searchPageGoodsVOList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public List<GoodsInfo> getGoodsByIds(List<Long> ids) {
        return goodsInfoMapper.selectByPrimaryKeys(ids);
    }

    @Override
    public List<GoodsCategory> getGoodsCategoryByIds(List<Long> ids) {
        return goodsCategoryMapper.selectByPrimaryKeys(ids);
    }

    @Override
    public int recoverStockNum(List<StockNumDTO> stockNumDTOS) {
        return goodsInfoMapper.recoverStockNum(stockNumDTOS);
    }


}
