package com.github.kingschan1204.easycrawl.core.agent;

import org.jsoup.Connection;

import java.io.Serializable;
import java.util.Map;

/**
 * @author kings.chan
 * 2019-03-07 9:33
 **/

public class AgentResult implements Serializable {

    public AgentResult(Long millis, Connection.Response response) {
        this.timeMillis = System.currentTimeMillis() - millis;
        this.response = response;
    }

    //请求耗时  毫秒
    private final Long timeMillis;
    private final Connection.Response response;
    // 对外
//    private Integer statusCode;
//    private String statusMessage;
//    private String charset;
//    private String contentType;
//    private String body;
//    private byte[] bodyAsByes;
//    private Map<String, String> cookies;
//    private Map<String, String> headers;

    public Long getTimeMillis() {
        return timeMillis;
    }

    public Integer getStatusCode() {
        return this.response.statusCode();
    }

    public String getStatusMessage() {
        return this.response.statusMessage();
    }

    public String getCharset() {
        return this.response.charset();
    }

    public String getContentType() {
        return this.response.contentType();
    }

    public String getBody() {
        return this.response.body();
    }

    public Map<String, String> getCookies() {
        return this.response.cookies();
    }

    public byte[] getBodyAsByes() {
        return this.response.bodyAsBytes();
    }

    public Map<String, String> getHeaders() {
        return this.response.headers();
    }

    /**
     * 获取文本内容
     * @param charset 将byte数组按传入编码转码
     * @return getContent
     */
    public String getContent(String charset) {
        try {
            return new String(getBodyAsByes(), charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
