package com.github.kingschan1204.easycrawl.core.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;

import java.io.Serializable;
import java.util.Map;

/**
 * @author kings.chan
 * 2019-03-07 9:33
 **/
@Slf4j
public class AgentResult implements Serializable {

    public AgentResult(Long millis, Connection.Response response) {
        this.timeMillis = System.currentTimeMillis() - millis;
        this.response = response;
        this.charset = response.charset();
        this.body = response.body();
    }

    //请求耗时  毫秒
    private final Long timeMillis;
    private final Connection.Response response;
    // 对外
//    private Integer statusCode;
//    private String statusMessage;
    @Setter
    private String charset;
    //    private String contentType;
    @Getter
    @Setter
    private String body;
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
        return charset;
    }

    public String getContentType() {
        return this.response.contentType();
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


}
