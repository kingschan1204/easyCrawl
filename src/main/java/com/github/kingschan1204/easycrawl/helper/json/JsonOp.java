package com.github.kingschan1204.easycrawl.helper.json;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

/**
 * @author kingschan
 * 2023-4-25
 * json操作兼容 jsonObject、jsonArray
 */
public interface JsonOp {

    JsonHelper put(String key, Object value);

    JsonHelper add(JSONObject jsonObject);

    <T> T get(String expression, Class<T> clazz);

    boolean hasKey(String expression);

    Set<String> getKeys();
}
