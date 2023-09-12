package com.tsong.cmall.admin.goods.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
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
import com.tsong.cmall.goods.web.vo.SearchPageGoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.tsong.cmall.goods.es.constants.GoodsConstants.GOODS_MAPPING;
import static com.tsong.cmall.goods.es.constants.GoodsConstants.INDEX;

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
    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public PageResult getGoodsListPage(PageQueryUtil pageUtil) {
        List<GoodsInfo> goodsList = adminGoodsMapper.selectGoodsList(pageUtil);
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
            insertDocIntoES(goodsInfo);
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
            updateDoc(goodsEditParam);
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
        Boolean res = adminGoodsMapper.batchUpdateSaleStatus(ids, saleStatus, adminId) > 0;
        if (res) {
            List<GoodsInfo> goodsInfoList = adminGoodsMapper.selectByPrimaryKeys(List.of(ids));
            if (saleStatus == Constants.SALE_STATUS_UP) {
                batchInsertDoc(goodsInfoList);
            } else {
                batchDelDoc(goodsInfoList);
            }
        }
        return res;
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

    private void insertDocIntoES(GoodsInfo goodsInfo) {
        genIndexIfNotExisted();
        SearchPageGoodsVO searchPageGoodsVO = new SearchPageGoodsVO(goodsInfo);
        // 1. 准备Request对象
        IndexRequest<SearchPageGoodsVO> request = new IndexRequest.Builder<SearchPageGoodsVO>()
                .index(INDEX)
                .id(searchPageGoodsVO.getGoodsId().toString())
                .document(searchPageGoodsVO)
                .build();

        // 2. 发送请求
        try {
            esClient.index(request);
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    private void updateDoc(GoodsEditParam goodsEditParam) {
        genIndexIfNotExisted();
        UpdateRequest<SearchPageGoodsVO, GoodsEditParam> request = new UpdateRequest.Builder<SearchPageGoodsVO, GoodsEditParam>()
                .index(INDEX)
                .id(goodsEditParam.getGoodsId().toString())
                .doc(goodsEditParam)
                .build();
        // 3. 发送请求
        try {
            esClient.update(request, SearchPageGoodsVO.class);
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    private void batchDelDoc(List<GoodsInfo> goodsList) {
        genIndexIfNotExisted();
        List<SearchPageGoodsVO> searchPageGoodsVOS = goodsList.stream().map(SearchPageGoodsVO::new).toList();
        // 1.创建builder
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (SearchPageGoodsVO searchPageGoodsVO : searchPageGoodsVOS) {
            br.operations(op -> op
                    .delete(del -> del
                            .index(INDEX)
                            .id(searchPageGoodsVO.getGoodsId().toString())
                    )
            );
        }
        // 2. 创建request
        BulkRequest request = br.build();

        // 3. 处理请求
        try {
            BulkResponse response = esClient.bulk(request);
            if (response.errors()){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                for (BulkResponseItem item : response.items()) {
                    if (item.error() != null){
                        CMallException.fail(item.error().reason());
                    }
                }
            }
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    private void batchInsertDoc(List<GoodsInfo> goodsList) {
        genIndexIfNotExisted();
        List<SearchPageGoodsVO> searchPageGoodsVOS = goodsList.stream().map(SearchPageGoodsVO::new).toList();
        // 1.创建builder
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (SearchPageGoodsVO searchPageGoodsVO : searchPageGoodsVOS) {
            br.operations(op -> op
                    // 选择操作类型(传入操作类型的builder)[index, create, delete, update]
                    // index和create都是插入
                    // index：指定id，如id存在，查看version；否则新增
                    //          不指定version，覆盖，version++
                    //          指定version，如version不同：失败；如version相同：覆盖，version++
                    // create：指定id，如id存在，失败；否则新增
                    .index(idx -> idx
                            .index(INDEX)
                            .id(searchPageGoodsVO.getGoodsId().toString())
                            .document(searchPageGoodsVO)
                    )
            );
        }
        // 2. 创建request
        BulkRequest request = br.build();

        // 3. 处理请求
        BulkResponse response;
        try {
            response = esClient.bulk(request);
            if (response.errors()){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                for (BulkResponseItem item : response.items()) {
                    if (item.error() != null){
                        CMallException.fail(item.error().reason());
                    }
                }
            }
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    private void genIndexIfNotExisted(){
        if (!isIndexExisted(INDEX)){
            createIndex(INDEX);
            insertAllDoc();
        }
    }

    private boolean isIndexExisted(String index) {
        boolean exists = false;
        ExistsRequest request = new ExistsRequest.Builder()
                .index(index)
                .build();

        try {
            exists = esClient.indices().exists(request).value();
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        return exists;
    }

    private void createIndex(String index) {
        // 1.创建request对象
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                // 1.1 指定索引名称
                .index(index)
                // 1.2 dsl语句
                .withJson(GOODS_MAPPING)
                .build();
        // 2.发送请求 indices返回索引库操作的所有方法
        try {
            esClient.indices().create(request);
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    private void insertAllDoc() {
        List<GoodsInfo> goodsList = adminGoodsMapper.selectAll();
        List<SearchPageGoodsVO> searchPageGoodsVOS = goodsList.stream().map(SearchPageGoodsVO::new).toList();
        // 1.创建builder
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (SearchPageGoodsVO searchPageGoodsVO : searchPageGoodsVOS) {
            br.operations(op -> op
                    // 选择操作类型(传入操作类型的builder)[index, create, delete, update]
                    // index和create都是插入
                    // index：指定id，如id存在，查看version；否则新增
                    //          不指定version，覆盖，version++
                    //          指定version，如version不同：失败；如version相同：覆盖，version++
                    // create：指定id，如id存在，失败；否则新增
                    .index(idx -> idx
                            .index(INDEX)
                            .id(searchPageGoodsVO.getGoodsId().toString())
                            .document(searchPageGoodsVO)
                    )
            );
        }
        // 2. 创建request
        BulkRequest request = br.build();

        // 3. 处理请求
        BulkResponse response;
        try {
            response = esClient.bulk(request);
            if (response.errors()){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                for (BulkResponseItem item : response.items()) {
                    if (item.error() != null){
                        CMallException.fail(item.error().reason());
                    }
                }
            }
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }
}
