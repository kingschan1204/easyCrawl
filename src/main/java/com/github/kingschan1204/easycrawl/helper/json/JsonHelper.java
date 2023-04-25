package com.github.kingschan1204.easycrawl.helper.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;

import java.util.*;

public class JsonHelper {

    private JSONObject json;
    private JSONArray jsonArray;

    public JsonHelper(JSONObject json) {
        this.json = json;
    }

    public JsonHelper(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }


    public static JsonHelper of(Object object, Feature... feature) {
        assert null != object;
        List<Feature> featureList = new ArrayList<>();
        featureList.add(Feature.OrderedField);
        if (null != feature) {
            featureList.addAll(Arrays.asList(feature));
        }
        if (object instanceof String) {
            String text = object.toString();
            if (text.startsWith("[")) {
                return new JsonHelper(JSON.parseArray(text));
            }
            return new JsonHelper(JSON.parseObject(text, featureList.toArray(new Feature[]{})));
        } else if (object instanceof Collections) {
            return new JsonHelper(JSONArray.parseArray(JSON.toJSONString(object)));
        }
        return new JsonHelper(JSON.parseObject(JSON.toJSONString(object), featureList.toArray(new Feature[]{})));
    }


    public JsonHelper put(String key, Object value) {
        Assert.notNull(json, "当前主体数据为JSONArray无法使用此方法！");
        this.json.put(key, value);
        return this;
    }

    public JsonHelper add(JSONObject jsonObject) {
        Assert.notNull(jsonArray, "当前主体数据为JSONObject无法使用此方法！");
        this.jsonArray.add(jsonObject);
        return this;
    }

    public Object get(String key) {
        Assert.notNull(json, "当前主体数据为JSONArray无法使用此方法！");
        return this.json.get(key);
    }

    /**
     * 通过
     *  <p>属性. 的方式取 支持多层<p/>
     *  <p>$first 返回jsonArray的第一个对象<p/>
     *  <p>$last 返回jsonArray的最后一个对象<p/>
     *  <p>* 返回jsonArray的所有对象<p/>
     *  <p>,逗号分隔可获取jsonArray的多个字段组成新对象返回<p/>
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
            } else if (expression.equals("*")) {
                return js;
            } else {
                //从集合里抽 支持多字段以,逗号分隔
                String[] fields = expression.split(",");
                JSONArray result = new JSONArray();
                for (int i = 0; i < js.size(); i++) {
                    JSONObject json = new JSONObject(true);
                    for (String key : fields) {
                        json.put(key, js.getJSONObject(i).get(key));
                    }
                    result.add(json);
                }
                return result;
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

    public Set<String> getKeys() {
        return getAllKeys(this.json);
    }

    Set<String> getAllKeys(JSONObject jsonObject) {
        Set<String> keys = new LinkedHashSet<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                Set<String> subKeys = getAllKeys((JSONObject) value);
                for (String subKey : subKeys) {
                    keys.add(key + "." + subKey);
                }
            } else {
                keys.add(key);
            }
        }
        return keys;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(null == json ? jsonArray : json, SerializerFeature.SortField);
    }

    public static void main(String[] args) {
//        String data = "[0,1,2,3,4]";
//        String data = "[{'name':'a'},{'name':'b','array':[1,2,3,4,5]}]";
        String data = "{'name':'b','array':[1,2,3,4,5]}";
        JsonHelper helper = JsonHelper.of(data);
        System.out.println(helper.get("array.$last", Object.class));
    }
}
