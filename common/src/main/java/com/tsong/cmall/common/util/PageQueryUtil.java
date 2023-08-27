package com.tsong.cmall.common.util;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PageQueryUtil extends LinkedHashMap<String, Object> {
    // 当前页码
    private int page;
    // 每页条数
    private int limit;
    private String sidx; // 可能是排序的顺序
    private String order;

    public PageQueryUtil(Map<String, Object> params) {
        this.putAll(params);
        // 分页参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("start", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);
        this.sidx = (String) params.get("sidx");
        this.order = (String) params.get("order");
        if (sidx != null && !sidx.isBlank() && !sidx.isEmpty()) {
            this.put("sortField", this.sidx.replaceAll("[A-Z]", "_$0").toLowerCase());
            this.put("order", order);
        }
    }
}
