package com.github.kingschan1204.easycrawl.core.agent;


import lombok.Data;
import org.jsoup.Connection;

import java.net.Proxy;
import java.util.Map;

@Data
public class HttpEngine {

    public Map<String, String> cookie;

    public Map<String, String> head;

    public String referer;

    public String url;

    public Integer timeOut = 8000;

    public String useAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public String body;

    /**
     * 代理
     */
    //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    public Proxy proxy;
    /**
     * http 请求方式
     */
    public Method method = Method.GET;

    public static enum Method {
        GET,
        POST
//        PUT,
//        DELETE,
//        PATCH,
//        HEAD,
//        OPTIONS,
//        TRACE
        ;

    }
    public Connection.Method method(){
        Connection.Method m;
        switch (this.method) {
            case GET:
                m = Connection.Method.GET;
                break;
            case POST:
                m = Connection.Method.POST;
                break;
            default:
                throw new RuntimeException("目前只支持：get , post 方法！");
        }
        return m;
    }

}
