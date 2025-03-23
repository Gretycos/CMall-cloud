package com.tsong.cmall.admin.goods.canal;

import com.tsong.cmall.common.mq.MessageHandler;
import com.tsong.cmall.entity.GoodsInfo;
import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.GOODS_CHANGED;


/**
 * @Author Tsong
 * @Date 2025/2/12 16:26
 */
@CanalListener(tableName = "tb_goods_info")
@Slf4j
public class GoodsChangeListener implements EntryListener<GoodsInfo> {
    @Resource
    private MessageHandler messageHandler;

    @Override
    public void insert(GoodsInfo goods) {
        log.info("insert goods:{}", goods);
        Map<String, Object> params = new HashMap<>();
        params.put("op", 1);
        params.put("data", goods);
        messageHandler.sendMessage(CMALL_DIRECT, GOODS_CHANGED, params);
    }

    @Override
    public void update(GoodsInfo before, GoodsInfo after, Set<String> fields) {
        log.info("update goods before:{}", before);
        log.info("update goods after:{}", after);
        Map<String, Object> params = new HashMap<>();
        params.put("op", 2);
        params.put("data", after);
        messageHandler.sendMessage(CMALL_DIRECT, GOODS_CHANGED, params);
    }

    @Override
    public void delete(GoodsInfo goods) {
        log.info("delete goods:{}", goods);
        Map<String, Object> params = new HashMap<>();
        params.put("op", 0);
        params.put("data", goods);
        messageHandler.sendMessage(CMALL_DIRECT, GOODS_CHANGED, params);
    }
}
