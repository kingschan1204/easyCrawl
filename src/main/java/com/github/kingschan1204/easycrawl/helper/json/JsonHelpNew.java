package com.github.kingschan1204.easycrawl.helper.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;

import java.util.Collections;

public class JsonHelpNew {
    private JSONObject json;
    private JSONArray jsonArray;

    public JsonHelpNew(JSONObject json) {
        this.json = json;
    }

    public JsonHelpNew(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public static JsonHelpNew of(String text) {
        if (text.startsWith("[")) {
            return new JsonHelpNew(JSON.parseArray(text));
        }
        return new JsonHelpNew(JSON.parseObject(text, Feature.OrderedField));
    }

    public static JsonHelpNew of(Object object) {
        if (object instanceof Collections) {
            return new JsonHelpNew(JSONArray.parseArray(JSON.toJSONString(object)));
        }
        return new JsonHelpNew(JSON.parseObject(JSON.toJSONString(object), Feature.OrderedField));
    }

    public JsonHelpNew put(String key, Object value) {
        Assert.notNull(json, "当前主体数据为JSONArray无法使用此方法！");
        this.json.put(key, value);
        return this;
    }

    public JsonHelpNew add(JSONObject jsonObject) {
        Assert.notNull(jsonArray, "当前主体数据为JSONObject无法使用此方法！");
        this.jsonArray.add(jsonObject);
        return this;
    }

    public Object get(String key){
        Assert.notNull(json, "当前主体数据为JSONArray无法使用此方法！");
        return this.json.get(key);
    }

    /**
     * 通过 属性. 的方式取 支持多层
     *
     * @param expression 表达式  属性.属性
     * @param clazz      返回类型
     * @return 表达式的值
     */
    public <T> T get(String expression, Class<T> clazz) {
        String[] depth = expression.split("\\.");
        Object object = null;
        if (null != json) {
            object = getValByExpression(this.json, depth[0]);
        } else {
            object = getValByExpression(this.jsonArray, depth[0]);
        }
        for (int i = 1; i < depth.length; i++) {
            object = getValByExpression(object, depth[i]);
        }
        return (T) object;
    }

    private Object getValByExpression(Object object, String expression) {
        if (object instanceof JSONObject) {
            return ((JSONObject) object).get(expression);
        } else if (object instanceof JSONArray) {
            //jsonArray模式
            //如果是数字直接取下标，保留关键字：$first第一条 $last最后一条
            JSONArray js = (JSONArray) object;
            if (expression.matches("\\d+")) {
                return js.get(Integer.parseInt(expression));
            } else if (expression.matches("\\$first")) {
                return js.get(0);
            } else if (expression.matches("\\$last")) {
                return js.get(js.size() - 1);
            }
        }
        return null;
    }

    /**
     * 是否存在key
     *
     * @param expression 表达式
     * @return 是否包括key
     */
    public boolean hasKey(String expression) {
        return null != get(expression, Object.class);
    }

    public static void main(String[] args) {
//        String data = "[0,1,2,3,4]";
//        String data = "[{'name':'a'},{'name':'b','array':[1,2,3,4,5]}]";
        String data = "{'name':'b','array':[1,2,3,4,5]}";
        JsonHelpNew helper = JsonHelpNew.of(data);
        System.out.println(helper.get("array.$last", Object.class));
    }
}
