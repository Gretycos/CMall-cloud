package com.tsong.cmall.admin.goods.service;

import com.tsong.cmall.admin.goods.web.params.GoodsAddParam;
import com.tsong.cmall.admin.goods.web.params.GoodsEditParam;
import com.tsong.cmall.admin.goods.web.vo.GoodsAndCategoryVO;
import com.tsong.cmall.admin.goods.web.vo.GoodsCarouselVO;
import com.tsong.cmall.admin.goods.web.vo.GoodsNameVO;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.GoodsInfo;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/9/11 16:51
 */
public interface IAdminGoodsService {
    PageResult getGoodsListPage(PageQueryUtil pageUtil);

    String saveGoods(GoodsAddParam goodsAddParam, Long adminId);

    String updateGoods(GoodsEditParam goodsEditParam, Long adminId);

    GoodsAndCategoryVO getGoodsByIdAndAdminId(Long goodsId, Long adminId);

    Boolean batchUpdateSaleStatus(Long[] ids, int saleStatus, Long adminId);

    GoodsNameVO getGoodsName(Long goodsId, Long adminId);

    List<GoodsNameVO> getAllGoodsNameByAdminId(Long adminId);

    GoodsCarouselVO getGoodsCarouselById(Long goodsId, Long adminId);

    void updateGoodsES(GoodsInfo goodsInfo);

    void deleteGoodsES(GoodsInfo goodsInfo);

    void insertGoodsES(GoodsInfo goodsInfo);
}
