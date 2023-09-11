package com.tsong.cmall.admin.goods.service.impl;

import com.tsong.cmall.admin.goods.mapper.AdminGoodsCategoryMapper;
import com.tsong.cmall.admin.goods.mapper.AdminGoodsMapper;
import com.tsong.cmall.admin.goods.service.IAdminGoodsService;
import com.tsong.cmall.admin.goods.web.params.GoodsAddParam;
import com.tsong.cmall.admin.goods.web.params.GoodsEditParam;
import com.tsong.cmall.admin.goods.web.vo.GoodsAndCategoryVO;
import com.tsong.cmall.admin.goods.web.vo.GoodsCarouselVO;
import com.tsong.cmall.admin.goods.web.vo.GoodsNameVO;
import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.goods.enums.CategoryLevelEnum;
import com.tsong.cmall.goods.mapper.GoodsInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/9/11 16:51
 */
@Service
public class AdminGoodsService implements IAdminGoodsService {
    @Autowired
    private AdminGoodsMapper adminGoodsMapper;
    @Autowired
    private AdminGoodsCategoryMapper adminGoodsCategoryMapper;

    @Override
    public PageResult getGoodsListPage(PageQueryUtil pageUtil) {
        List<GoodsInfo> goodsList = adminGoodsMapper.getGoodsList(pageUtil);
        int total = adminGoodsMapper.getTotalGoods(pageUtil);
        return new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public String saveGoods(GoodsAddParam goodsAddParam, Long adminId) {
        GoodsInfo goodsInfo = new GoodsInfo();
        BeanUtil.copyProperties(goodsAddParam, goodsInfo);
        goodsInfo.setCreateUser(adminId);
        GoodsCategory goodsCategory = adminGoodsCategoryMapper.selectByPrimaryKey(goodsInfo.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null
                || goodsCategory.getCategoryLevel().intValue() != CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        // 查看是否已存在物品
        if (adminGoodsMapper.selectByCategoryIdAndName(goodsInfo.getGoodsName(), goodsInfo.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        // 插入
        if (adminGoodsMapper.insertSelective(goodsInfo) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoods(GoodsEditParam goodsEditParam, Long adminId) {
        GoodsInfo goods = new GoodsInfo();
        BeanUtil.copyProperties(goodsEditParam, goods);
        GoodsCategory goodsCategory = adminGoodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null
                || goodsCategory.getCategoryLevel().intValue() != CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        GoodsInfo temp = adminGoodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsInfo temp2 = adminGoodsMapper.selectByCategoryIdAndName(
                goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            // name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (adminGoodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsAndCategoryVO getGoodsByIdAndAdminId(Long goodsId, Long adminId) {
        GoodsInfo goodsInfo = adminGoodsMapper.selectByIdAndCreateUser(goodsId, adminId);
        if (goodsInfo == null) {
            CMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        GoodsAndCategoryVO goodsAndCategoryVO = new GoodsAndCategoryVO();
        BeanUtil.copyProperties(goodsInfo, goodsAndCategoryVO);
        return goodsAndCategoryVO;
    }

    @Override
    public Boolean batchUpdateSaleStatus(Long[] ids, int saleStatus, Long adminId) {
        return adminGoodsMapper.batchUpdateSaleStatus(ids, saleStatus, adminId) > 0;
    }

    @Override
    public GoodsNameVO getGoodsName(Long goodsId, Long adminId) {
        GoodsInfo goodsInfo = adminGoodsMapper.selectByIdAndCreateUser(goodsId, adminId);
        if (goodsInfo == null) {
            CMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        GoodsNameVO goodsNameVO = new GoodsNameVO();
        BeanUtil.copyProperties(goodsInfo, goodsNameVO);
        return goodsNameVO;
    }

    @Override
    public List<GoodsNameVO> getAllGoodsNameByAdminId(Long adminId) {
        List<GoodsInfo> goodsList = adminGoodsMapper.selectByCreateUser(adminId);
        return BeanUtil.copyList(goodsList, GoodsNameVO.class);
    }

    @Override
    public GoodsCarouselVO getGoodsCarouselById(Long goodsId, Long adminId) {
        GoodsInfo goods = adminGoodsMapper.selectByIdAndCreateUser(goodsId, adminId);
        if (Constants.SALE_STATUS_UP != goods.getGoodsSaleStatus()) {
            CMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        GoodsCarouselVO goodsCarouselVO = new GoodsCarouselVO();
        BeanUtil.copyProperties(goods, goodsCarouselVO);
        return goodsCarouselVO;
    }
}
