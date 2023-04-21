package com.github.kingschan1204.easycrawl.core.agent;


import java.net.Proxy;
import java.util.Map;

/**
 * @author kings.chan
 * 2020-02-10 15:03
 **/
@Deprecated
public interface WebAgent<T> {

    WebAgent<T> url(String url);

    WebAgent<T> referer(String referer);

    WebAgent<T> method(HttpRequestConfig.Method method);

    WebAgent<T> head(Map<String, String> head);

    WebAgent<T> useAgent(String useAgent);

    WebAgent<T> cookie(Map<String, String> cookie);

    WebAgent<T> timeOut(Integer timeOut);

    WebAgent<T> proxy(Proxy proxy);

    WebAgent<T> body(String body);

    WebAgent<T> folder(String folder);

    WebAgent<T> fileName(String fileName);

    /***
     * 每次运行时都指定数据
     * @param data
     * @return
     * @throws Exception
     */
    T execute(Map<String, Object> data) throws Exception;

}
