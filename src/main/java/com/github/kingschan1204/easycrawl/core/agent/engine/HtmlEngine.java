package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.AgentResult;
import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.core.variable.ScanVariable;

import java.net.Proxy;
import java.util.Map;

public final class HtmlEngine extends HttpEngine implements WebAgent<AgentResult> {


    @Override
    public WebAgent<AgentResult> url(String url) {
        this.setUrl(url);
        return this;
    }

    @Override
    public WebAgent<AgentResult> referer(String referer) {
        this.setReferer(referer);
        return this;
    }

    @Override
    public WebAgent<AgentResult> method(HttpEngine.Method method) {
        this.setMethod(method);
        return this;
    }

    @Override
    public WebAgent<AgentResult> head(Map<String, String> head) {
        this.setHead(head);
        return this;
    }

    @Override
    public WebAgent<AgentResult> useAgent(String useAgent) {
        this.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebAgent<AgentResult> cookie(Map<String, String> cookie) {
        this.setCookie(cookie);
        return this;
    }

    @Override
    public WebAgent<AgentResult> timeOut(Integer timeOut) {
        this.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebAgent<AgentResult> proxy(Proxy proxy) {
        this.setProxy(proxy);
        return this;
    }

    @Override
    public WebAgent<AgentResult> body(String body) {
        this.setBody(body);
        return this;
    }

    @Override
    public AgentResult execute(Map<String, Object> data) throws Exception {
        String httpUrl = ScanVariable.parser(this.url, data).trim();
        return JsoupHelper.request(
                httpUrl, this.method(),
                this.timeOut, this.useAgent, this.referer, this.head,
                this.cookie, this.proxy,
                true, true,this.body);
    }

    @Override
    public HttpEngine get() {
        return this;
    }
}
