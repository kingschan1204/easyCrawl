package com.github.kingschan1204.easycrawl.helper.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import lombok.extern.slf4j.Slf4j;

/**
 * json 操作统一标准工具类
 *
 * @author kings.chan
 * @create 2020-02-18 14:23
 **/
@Slf4j
public class JsonHelper {

    private JSONObject json;

    public JsonHelper() {
        this.json = new JSONObject(true);
    }

    public JsonHelper(Object object) {
        this.json = new JSONObject(true);
        json = JSON.parseObject(JSON.toJSONString(object));
    }

    /**
     * 统过一个字符串实例化对象
     * @param jsonStr
     */
    public JsonHelper(String jsonStr) {
        this.json = JSON.parseObject(jsonStr, Feature.OrderedField);
    }

    /**
     * 根据键得到对象
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return this.json.get(key);
    }

    /**
     * 通过 属性. 的方式取 支持多层
     *
     * @param expression 表达式  属性.属性
     * @param clazz      返回类型
     * @return
     */
    public <T> T getObject(String expression, Class<T> clazz) {
        String depth[] = expression.split("\\.");
        Object object = this.json.get(depth[0]);
        for (int i = 1; i < depth.length; i++) {
            object = getValByExpression(object, depth[i]);
        }
        return (T) object;
    }

    private Object getValByExpression(Object object, String expression) {
        if (object instanceof JSONObject) {
            return ((JSONObject) object).get(expression);
        } else if (object instanceof JSONArray) {
            return ((JSONArray) object).get(Integer.parseInt(expression));
        }
        return null;
    }

    /**
     * 是否存在key
     *
     * @param expression 表达式
     * @return
     */
    public boolean hasKey(String expression) {
        String[] depth = expression.split("\\.");
        Object object = this.json.get(depth[0]);
        for (int i = 1; i < depth.length; i++) {
            object = getValByExpression(object, depth[i]);
        }
        return null != object;
    }

    /**
     * 赋值
     *
     * @param key
     * @param value
     * @return
     */
    public JsonHelper put(String key, Object value) {
        this.json.put(key, value);
        return this;
    }

    /**
     * 格式化赋值 主要处理  整型和浮点型
     *
     * @param key
     * @param value
     * @return
     */
    public JsonHelper putFormat(String key, String value, Class clazz) {
        if (clazz.equals(Double.class)) {
            this.json.put(key, Double.parseDouble(value.replaceAll("[^0-9|.|-]", "")));
        } else if (clazz.equals(Integer.class)) {
            this.json.put(key, Integer.parseInt(value.replaceAll("[^0-9|-]", "")));
        } else {
            this.json.put(key, value);
        }
        return this;
    }

    /**
     * 简单移除key
     *
     * @param keys
     */
    public void removeKey(String... keys) {
        for (String key : keys) {
            this.json.remove(key);
        }
    }

    public JSONObject toJson() {
        return this.json;
    }


    public static void main(String[] args) {
        String json = "{name:'kingschan',website:{name:'webname',test:{a:'b'},tag:[0,1,2,3],arrays:[{age:12},{age:13},{age:14}]}}";
        JsonHelper jsonOperation = new JsonHelper(json);
        System.out.println(jsonOperation.getObject("website.test", JSONObject.class));
        System.out.println(jsonOperation.getObject("website.tag", JSONArray.class));
        System.out.println(jsonOperation.getObject("website.tag.1", Integer.class));
        System.out.println(jsonOperation.getObject("website.test.a", String.class));
        System.out.println(jsonOperation.getObject("website.arrays.1.age", Integer.class));
        System.out.println(jsonOperation.hasKey("website.arrays.1.age"));
    }

}
