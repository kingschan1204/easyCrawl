package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebDataAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.core.agent.utils.WebPage;
import com.github.kingschan1204.easycrawl.core.variable.ScanVariable;
import org.jsoup.Connection;

import java.net.Proxy;
import java.util.Map;

public final class HtmlEngine extends HttpEngine implements WebDataAgent<WebPage> {


    @Override
    public WebDataAgent<WebPage> url(String url) {
        this.setUrl(url);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> referer(String referer) {
        this.setReferer(referer);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> method(HttpEngine.Method method) {
        this.setMethod(method);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> head(Map<String, String> head) {
        this.setHead(head);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> useAgent(String useAgent) {
        this.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> cookie(Map<String, String> cookie) {
        this.setCookie(cookie);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> timeOut(Integer timeOut) {
        this.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> proxy(Proxy proxy) {
        this.setProxy(proxy);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> body(String body) {
        this.setBody(body);
        return this;
    }

    @Override
    public WebPage dataPull(Map<String, Object> data) throws Exception {
        String httpUrl = ScanVariable.parser(this.url, data).trim();
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
        return JsoupHelper.request(
                httpUrl, m,
                this.timeOut, this.useAgent, this.referer, this.head,
                this.cookie, this.proxy,
                true, true,this.body);
    }

    @Override
    public HttpEngine get() {
        return this;
    }
}
