package com.github.kingschan1204.easycrawl.core.agent;

import com.github.kingschan1204.easycrawl.core.agent.interceptor.AfterInterceptor;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 通用http1.1代理模式
 *
 * @author kingschan
 * 2023-4-25
 */
@Slf4j
public class GenericHttp1AgentProxy implements WebAgent {

    private WebAgent webAgent;
    private AgentResult result;
    private List<AfterInterceptor> interceptors;

    public GenericHttp1AgentProxy(WebAgent webAgent, AfterInterceptor... afterInterceptors) {
        this.webAgent = webAgent;
        this.interceptors = Arrays.asList(afterInterceptors);
    }

    @Override
    public HttpRequestConfig getConfig() {
        return this.webAgent.getConfig();
    }

    @Override
    public WebAgent config(HttpRequestConfig config) {
        if (null != config) {
            this.webAgent.config(config);
        }
        return this;
    }

    @Override
    public WebAgent url(String url) {
        this.webAgent.url(url);
        return this;
    }

    @Override
    public WebAgent referer(String referer) {
        this.webAgent.referer(referer);
        return this;
    }

    @Override
    public WebAgent method(HttpRequestConfig.Method method) {
        this.webAgent.method(method);
        return this;
    }

    @Override
    public WebAgent head(Map<String, String> head) {
        this.webAgent.head(head);
        return this;
    }

    @Override
    public WebAgent head(String key, String value) {
        this.webAgent.head(key, value);
        return this;
    }

    @Override
    public WebAgent useAgent(String useAgent) {
        this.webAgent.useAgent(useAgent);
        return this;
    }

    @Override
    public WebAgent cookie(Map<String, String> cookie) {
        this.webAgent.cookie(cookie);
        return this;
    }

    @Override
    public WebAgent cookie(String key, String value) {
        this.webAgent.cookie(key, value);
        return this;
    }

    @Override
    public WebAgent timeOut(Integer timeOut) {
        this.webAgent.timeOut(timeOut);
        return this;
    }

    @Override
    public WebAgent proxy(Proxy proxy) {
        this.webAgent.proxy(proxy);
        return this;
    }

    @Override
    public WebAgent proxy(Proxy.Type type, String host, int port) {
        this.webAgent.proxy(type, host, port);
        return this;
    }

    @Override
    public WebAgent body(String body) {
        this.webAgent.body(body);
        return this;
    }

    @Override
    public WebAgent folder(String folder) {
        this.webAgent.folder(folder);
        return this;
    }

    @Override
    public WebAgent fileName(String fileName) {
        this.webAgent.fileName(fileName);
        return this;
    }

    @Override
    public WebAgent execute(Map<String, Object> data) {
        WebAgent wa = this.webAgent.execute(data);
        for (AfterInterceptor interceport : interceptors) {
            result = interceport.interceptor(data, wa);
        }
        return this;
    }

    @Override
    public AgentResult getResult() {
        //优先拿自身对象的agentResult
        return null == result ? this.webAgent.getResult() : result;
    }

    @Override
    public JsonHelper getJson() {
        return this.webAgent.getJson();
    }

    @Override
    public String getText() {
        return this.webAgent.getText();
    }

    @Override
    public File getFile() {
        return this.webAgent.getFile();
    }
}
