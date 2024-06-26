package com.github.kingschan1204.easycrawl.helper.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author kings.chan
 * 2024-6-26
 */
public interface JsonHelper {

    static JsonHelper of(Object object){
        return EasyJson.of(object);
    }

    /**
     * 转成java对象
     * @param clazz 类型
     * @return
     * @param <T>
     */
     <T> T toJavaObj(Class<T> clazz);

    /**
     * 转成集合java 对象
     * @param clazz 类型
     * @return
     * @param <T>
     */
    <T> List<T> toListObj(Class<T> clazz);


    /**
     * 根据表达式返回一个新的对象
     * @param expression
     * @return
     */
    EasyJson op(String expression);

    /**
     * 根据表达式获取值
     * @param expression 表达式
     * @param clazz 类型
     * @return
     * @param <T>
     */
    <T> T get(String expression, Class<T> clazz);

    /**
     * 在表达式指定位置设置值
     * @param exp
     * @param key
     * @param value
     * @return
     */
    JsonHelper put(String exp, String key, Object value);

    /**
     * 设置值
     * @param key
     * @param value
     * @return
     */
    JsonHelper put(String key, Object value);

    void forEach(Consumer<? super JsonNode> consumer);
}
