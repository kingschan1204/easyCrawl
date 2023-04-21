package com.github.kingschan1204.easycrawl.core.agent;


import lombok.Data;
import org.jsoup.Connection;

import java.net.Proxy;
import java.util.Map;

@Data
public class HttpRequestConfig {

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


    //下载文件相关配置
    /**
     * 下载文件保存的目录
     */
    private String folder;
    /**
     * 写入磁盘时的文件名，不传值的时候自动识别，传值的时候用手动指定的文件名
     */
    private String fileName;




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
