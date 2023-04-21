package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.WebAgentNew;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EasyCrawl<R> {

    private WebAgentNew webAgent;
    private Function<WebAgentNew, R> parserFunction;

    public EasyCrawl<R> webAgent(WebAgentNew webAgent) {
        this.webAgent = webAgent;
        return this;
    }

//    public static EasyCrawlNew of(WebAgentNew webAgent){
//        return new EasyCrawlNew(webAgent);
//    }

    public EasyCrawl<R> analyze(Function<WebAgentNew, R> parserFunction) {
        this.parserFunction = parserFunction;
        return this;
    }

    public R execute() {
        return execute(null);
    }

    public R execute(Map<String, Object> map) {
        Assert.notNull(webAgent, "agent对象不能为空！");
        Assert.notNull(parserFunction, "解析函数不能为空！");
        R result;
        CompletableFuture<R> cf = CompletableFuture.supplyAsync(() -> {
            try {
                return webAgent.execute(map);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenApply(parserFunction);
        try {
            result = cf.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
