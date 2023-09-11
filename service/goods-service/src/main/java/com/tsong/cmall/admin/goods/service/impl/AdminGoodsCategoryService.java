package com.tsong.cmall.admin.goods.service.impl;

import com.tsong.cmall.admin.goods.mapper.AdminGoodsCategoryMapper;
import com.tsong.cmall.admin.goods.service.IAdminGoodsCategoryService;
import com.tsong.cmall.admin.goods.web.params.GoodsCategoryAddParam;
import com.tsong.cmall.admin.goods.web.params.GoodsCategoryEditParam;
import com.tsong.cmall.admin.goods.web.vo.CategoryNamesVO;
import com.tsong.cmall.admin.goods.web.vo.CategoryResultVO;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.goods.enums.CategoryLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * @Author Tsong
 * @Date 2023/3/23 15:13
 */
@Service
public class AdminGoodsCategoryService implements IAdminGoodsCategoryService {
    @Autowired
    private AdminGoodsCategoryMapper adminGoodsCategoryMapper;

    @Override
    public PageResult getCategoriesPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = adminGoodsCategoryMapper.selectGoodsCategoryList(pageUtil);
        int total = adminGoodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        return new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public String saveCategory(GoodsCategoryAddParam goodsCategoryAddParam) {
        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryAddParam, goodsCategory);
        GoodsCategory temp = adminGoodsCategoryMapper.selectByLevelAndName(
                goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        // 已存在
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        // 成功
        if (adminGoodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        // 失败
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategoryEditParam goodsCategoryEditParam) {
        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryEditParam, goodsCategory);
        GoodsCategory temp = adminGoodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        // 不存在
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = adminGoodsCategoryMapper.selectByLevelAndName(
                goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            // 同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (adminGoodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return adminGoodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return adminGoodsCategoryMapper.deleteBatch(ids) > 0;
    }


    @Override
    public CategoryResultVO getCategoriesForSelect(Long categoryId) {
        GoodsCategory category = adminGoodsCategoryMapper.selectByPrimaryKey(categoryId);
        //既不是一级分类也不是二级分类则为不返回数据
        if (category == null || category.getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            CMallException.fail("参数异常！");
        }
        CategoryResultVO categoryResultVO = new CategoryResultVO();
        if (category.getCategoryLevel() == CategoryLevelEnum.LEVEL_ONE.getLevel()) {
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = this.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(categoryId), CategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = this.selectByLevelAndParentIdsAndNumber(
                        Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResultVO.setSecondLevelCategories(secondLevelCategories);
                categoryResultVO.setThirdLevelCategories(thirdLevelCategories);
            }
        }
        if (category.getCategoryLevel() == CategoryLevelEnum.LEVEL_TWO.getLevel()) {
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = this.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(categoryId), CategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResultVO.setThirdLevelCategories(thirdLevelCategories);
        }
        return categoryResultVO;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return adminGoodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0); // 0代表查询所有
    }

    @Override
    public List<CategoryNamesVO> getAllLevel3Categories() {
        List<GoodsCategory> categoryList = adminGoodsCategoryMapper.selectByLevel(3);
        return BeanUtil.copyList(categoryList, CategoryNamesVO.class);
    }
}
