package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.variable.ScanVariable;
import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebDataAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.core.agent.utils.WebPage;
import org.jsoup.Connection;

import java.io.File;
import java.net.Proxy;
import java.util.Map;

public final class HtmlEngine implements WebDataAgent<WebPage> {

    HttpEngine engine = new HttpEngine();

    @Override
    public WebDataAgent<WebPage> url(String url) {
        this.engine.setUrl(url);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> referer(String referer) {
        this.engine.setReferer(referer);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> method(HttpEngine.Method method) {
        this.engine.setMethod(method);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> head(Map<String, String> head) {
        this.engine.setHead(head);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> useAgent(String useAgent) {
        this.engine.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> cookie(Map<String, String> cookie) {
        this.engine.setCookie(cookie);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> timeOut(Integer timeOut) {
        this.engine.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> proxy(Proxy proxy) {
        this.engine.setProxy(proxy);
        return this;
    }

    @Override
    public WebDataAgent<WebPage> body(String body) {
        this.engine.setBody(body);
        return this;
    }

    @Override
    public WebPage dataPull(Map<String, Object> data) throws Exception {
        String httpUrl = ScanVariable.parser(this.engine.url, data).trim();
        Connection.Method m;
        switch (this.engine.method) {
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
                this.engine.timeOut, this.engine.useAgent, this.engine.referer, this.engine.head,
                this.engine.cookie, this.engine.proxy,
                true, true,this.engine.body);
    }

    @Override
    public HttpEngine get() {
        return this.engine;
    }
}
