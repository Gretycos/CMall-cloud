package com.tsong.cmall.goods.es.constants;

import java.io.Reader;
import java.io.StringReader;

/**
 * @Author Tsong
 * @Date 2023/9/3 00:12
 */
public class GoodsConstants {
    public static final String INDEX = "goods";
    public static final Reader GOODS_MAPPING = new StringReader("""
            {
              "settings": {
                "analysis": {
                  "analyzer": {
                    "text_analyzer": {
                      "tokenizer": "ik_max_word",
                      "filter": "pinyin"
                    },
                    "completion_analyzer":{
                      "tokenizer": "keyword",
                      "filter": "pinyin"
                    }
                  },
                  "filter": {
                    "pinyin": {
                      "type": "pinyin",
                      "keep_full_pinyin": false,
                      "keep_joined_full_pinyin": true,
                      "keep_original": true,
                      "limit_first_letter_length": 16,
                      "remove_duplicated_term": true,
                      "none_chinese_pinyin_tokenize": false
                    }
                  }
                }
              },
              "mappings":{
                "properties": {
                  "goodsId":{
                    "type": "keyword",
                    "index": false
                  },
                  "goodsName":{
                    "type": "text",
                    "analyzer": "text_analyzer",
                    "search_analyzer": "ik_smart",
                    "copy_to": "all"
                  },
                  "goodsIntro":{
                    "type": "keyword",
                    "index": false
                  },
                  "goodsCoverImg":{
                    "type": "keyword",
                    "index": false
                  },
                  "sellingPrice":{
                    "type": "double"
                  },
                  "tag":{
                    "type": "text",
                    "analyzer": "text_analyzer",
                    "search_analyzer": "ik_smart",
                    "copy_to": "all"
                  },
                  "all":{
                    "type": "text",
                    "analyzer": "text_analyzer",
                    "search_analyzer": "ik_smart"
                  },
                  "suggestion":{
                    "type": "completion",
                    "analyzer": "completion_analyzer"
                  }
                }
              }
            }
            """);
}
