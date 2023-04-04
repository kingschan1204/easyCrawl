package com.github.kingschan1204.easycrawl.core.agent;


import java.net.Proxy;
import java.util.Map;

/**
 * @author kings.chan
 * @create 2020-02-10 15:03
 **/

public interface WebDataAgent<T> {

    WebDataAgent<T> url(String url);

    WebDataAgent<T> referer(String referer);

    WebDataAgent<T> method(HttpEngine.Method method);

    WebDataAgent<T> head(Map<String, String> head);

    WebDataAgent<T> useAgent(String useAgent);

    WebDataAgent<T> cookie(Map<String, String> cookie);

    WebDataAgent<T> timeOut(Integer timeOut);

    WebDataAgent<T> proxy(Proxy proxy);


    /***
     * 每次运行时都指定数据
     * @param data
     * @return
     * @throws Exception
     */
    T dataPull(Map<String, Object> data) throws Exception;

    HttpEngine get();


}
