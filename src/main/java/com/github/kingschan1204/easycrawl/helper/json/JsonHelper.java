package com.github.kingschan1204.easycrawl.helper.json;


import com.alibaba.fastjson2.*;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;

import java.util.*;
/**
 * @author kingschan
 * 2023-4-25
 * json操作兼容 jsonObject、jsonArray
 */
public class JsonHelper implements JsonOp{

    private JSONObject json;
    private JSONArray jsonArray;

    public JsonHelper(JSONObject json) {
        this.json = json;
    }

    public JsonHelper(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }


    public static JsonHelper of(Object object, JSONReader.Feature... feature) {
        assert null != object;
        List<JSONReader.Feature> featureList = new ArrayList<>();
//        featureList.add(JSONReader.Feature.);
        if (null != feature) {
            featureList.addAll(Arrays.asList(feature));
        }
        if (object instanceof String) {
            String text = object.toString();
            if (text.startsWith("[")) {
                return new JsonHelper(JSON.parseArray(text));
            }
            return new JsonHelper(JSON.parseObject(text, featureList.toArray(new JSONReader.Feature[]{})));
        } else if (object instanceof Collections) {
            return new JsonHelper(JSONArray.parseArray(JSON.toJSONString(object)));
        }
        return new JsonHelper(JSON.parseObject(JSON.toJSONString(object), featureList.toArray(new JSONReader.Feature[]{})));
    }

    @Override
    public JsonHelper put(String key, Object value) {
        Assert.notNull(json, "当前主体数据为JSONArray无法使用此方法！");
        this.json.put(key, value);
        return this;
    }

    @Override
    public JsonHelper add(com.alibaba.fastjson.JSONObject jsonObject) {
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
     *  <p>->arrayList抽取单个字段转成 arrayList类似 [1,2,3,4]<p/>
     * @param expression 表达式  属性.属性
     * @param clazz      返回类型
     * @return 表达式的值
     */
    @Override
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
            }
            // 抽取单个字段转成 arrayList类似 [1,2,3,4]
            else if(expression.matches("\\w+(->)arrayList$")){
                String key = expression.replace("->arrayList","");
                JSONArray list = new JSONArray(js.size());
                for (int i = 0; i < js.size(); i++) {
                    list.add(js.getJSONObject(i).get(key));
                }
                return list;
            }else {
                //从集合里抽 支持多字段以,逗号分隔
                String[] fields = expression.split(",");
                JSONArray result = new JSONArray();
                for (int i = 0; i < js.size(); i++) {
                    JSONObject json = new JSONObject();
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
    @Override
    public boolean hasKey(String expression) {
        return null != get(expression, Object.class);
    }

    @Override
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
        //按顺序输出，默认不输出为null的字段，设置为null也输出
        return JSON.toJSONString(null == json ? jsonArray : json, JSONWriter.Feature.MapSortField,JSONWriter.Feature.WriteNulls);
    }

    public static void main(String[] args) {
//        String data = "[0,1,2,3,4]";
//        String data = "[{'name':'a'},{'name':'b','array':[1,2,3,4,5]}]";
        String data = "{'name':'b','array':[1,2,3,4,5],'roles':[{'id':1,name:'a1'},{'id':2,name:'a2'}]}";
        JsonHelper helper = JsonHelper.of(data);
        System.out.println(helper.get("roles.id->arrayList", Object.class));
    }
}
