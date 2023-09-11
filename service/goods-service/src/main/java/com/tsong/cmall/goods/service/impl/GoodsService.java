package com.tsong.cmall.goods.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tsong.cmall.goods.es.constants.GoodsConstants.GOODS_MAPPING;
import static com.tsong.cmall.goods.es.constants.GoodsConstants.INDEX;

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

    @Autowired
    private ElasticsearchClient esClient;


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
        List<SearchPageGoodsVO> searchPageGoodsVOList = null;
        if (!isIndexExisted(INDEX)){
//            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            createIndex(INDEX);
            insertDoc();
        }
        // 1. 准备request
        SearchRequest.Builder rb = new SearchRequest.Builder();
        rb.index(INDEX);

        // 2. DSL
        // 2.1 query
        getBasicQueryBuilder(rb, pageUtil);


        // 2.2 分页
        int page = pageUtil.getPage();
        int size = pageUtil.getLimit();
        rb.from((page - 1) * size).size(size);

        SortOrder order = pageUtil.get("order").toString().equals("ASC") ? SortOrder.Asc : SortOrder.Desc;
        // 2.3 排序
        rb.sort(sb -> sb
                .field(fb -> fb
                        .field("sellingPrice")
                        .order(order)
                )
        );

        // 3. 发请求
        SearchRequest request = rb.build();
        try{
            SearchResponse<SearchPageGoodsVO> response = esClient.search(request, SearchPageGoodsVO.class);
            // 4. 解析结果
            searchPageGoodsVOList = handleResponse(response);
            int total = searchPageGoodsVOList.size();
            return new PageResult(searchPageGoodsVOList, total, pageUtil.getLimit(), pageUtil.getPage());
        }catch (IOException e){
            CMallException.fail("查询失败！");
            return new PageResult(searchPageGoodsVOList, 0, pageUtil.getLimit(), pageUtil.getPage());
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        List<String> res = new ArrayList<>();
        try {
            // 1. request
            SearchRequest request = SearchRequest.of(r -> r
                    // 2. DSL
                    .index(INDEX)
                    .suggest(s -> s
                            .suggesters("suggestions", fs -> fs
                                    .prefix(prefix)
                                    .completion(c -> c
                                            .field("suggestion")
                                            .skipDuplicates(true)
                                            .size(10)
                                    )
                            )
                    )

            );
            // 3. 发请求
            SearchResponse<SearchPageGoodsVO> response = esClient.search(request, SearchPageGoodsVO.class);
            // 4. 解析结果
            Map<String, List<Suggestion<SearchPageGoodsVO>>> suggest = response.suggest();
            List<Suggestion<SearchPageGoodsVO>> suggestions = suggest.get("suggestions");
            for (Suggestion<SearchPageGoodsVO> suggestion : suggestions) {
                for (CompletionSuggestOption<SearchPageGoodsVO> option : suggestion.completion().options()) {
                    String text = option.text();
                    res.add(text);
                }
            }
        } catch (IOException e) {
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        return res;
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

    @Override
    public void decreaseStockNum(List<StockNumDTO> stockNumDTOS) {
        goodsInfoMapper.decreaseStockNum(stockNumDTOS);
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

    private void insertDoc() {
        List<GoodsInfo> goodsList = goodsInfoMapper.selectAll();
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

    private void getBasicQueryBuilder(SearchRequest.Builder rb, PageQueryUtil pageUtil) {
        // 构建BooleanQuery
        BoolQuery.Builder bq = new BoolQuery.Builder();

        // 关键字搜索
        String key = (String) pageUtil.get("keyword");

        if (!StringUtils.hasText(key)){
            bq.must(q -> q.matchAll(ma -> ma));
        } else {
            bq.must(q -> q
                    .match(m -> m
                            .field("all")
                            .query(key)
                    )
            );
        }

        // 条件过滤
        Integer minPrice = (Integer) pageUtil.get("minPrice");
        Integer maxPrice = (Integer) pageUtil.get("maxPrice");
        Long goodsCategoryId = (Long) pageUtil.get("goodsCategoryId");

        // price
        if (minPrice != null && maxPrice != null) {
            bq.filter(f -> f
                    .range(r -> r
                            .field("sellingPrice")
                            .gte(JsonData.of(minPrice))
                            .lte(JsonData.of(maxPrice))
                    )
            );
        }

        // category
        if (goodsCategoryId != null) {
            bq.filter(f -> f
                    .term(t -> t
                            .field("goodsCategoryId")
                            .value(goodsCategoryId))
            );
        }

        // 算分
        rb.query(q -> q
                // 使用算分查询
                .functionScore(fs -> fs
                        // 基础查询
                        .query(fsq -> fsq.bool(bq.build()))
                        // 算分函数
                        .functions(func -> func
                                // 过滤条件
                                .filter(fil -> fil
                                        .term(t -> t
                                                .field("isAD")
                                                .value(true)
                                        )
                                )
                                // 算分数值：权重
                                .weight(10.0)
                        )
                        // 算分方式：乘法（默认）
                        .boostMode(FunctionBoostMode.Multiply)
                )
        );
    }

    private List<SearchPageGoodsVO> handleResponse(SearchResponse<SearchPageGoodsVO> response) {
        HitsMetadata<SearchPageGoodsVO> searchHits = response.hits();
        // 结果数组
        List<Hit<SearchPageGoodsVO>> hits = searchHits.hits();
        // 返回值
        List<SearchPageGoodsVO> searchPageGoodsVOS = new ArrayList<>();
        for (Hit<SearchPageGoodsVO> hit : hits) {
            searchPageGoodsVOS.add(hit.source());
        }
        return searchPageGoodsVOS;
    }
}
