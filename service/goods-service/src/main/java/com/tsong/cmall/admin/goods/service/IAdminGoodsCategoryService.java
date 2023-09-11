package com.tsong.cmall.admin.goods.service;

import com.tsong.cmall.admin.goods.web.params.GoodsCategoryAddParam;
import com.tsong.cmall.admin.goods.web.params.GoodsCategoryEditParam;
import com.tsong.cmall.admin.goods.web.vo.CategoryNamesVO;
import com.tsong.cmall.admin.goods.web.vo.CategoryResultVO;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.GoodsCategory;

import java.util.List;

public interface IAdminGoodsCategoryService {
    /**
     * @Description 分页
     * @Param [pageUtil]
     * @Return com.tsong.cmall.util.PageResult
     */
    PageResult getCategoriesPage(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategoryAddParam goodsCategoryAddParam);

    String updateGoodsCategory(GoodsCategoryEditParam goodsCategoryEditParam);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Long[] ids);

    /**
     * @Description 获取选择的分类，用于三级分类联动
     * @Param [categoryId]
     * @Return com.tsong.cmall.controller.vo.CategoryResultVO
     */
    CategoryResultVO getCategoriesForSelect(Long categoryId);

    /**
     * @Description 根据parentId和level获取分类列表
     * @Param [parentIds, categoryLevel]
     * @Return java.util.List<com.tsong.cmall.entity.GoodsCategory>
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    List<CategoryNamesVO> getAllLevel3Categories();
}
