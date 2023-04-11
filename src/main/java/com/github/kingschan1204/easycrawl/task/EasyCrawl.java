package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EasyCrawl<T, R> implements CrawlTask<T, R> {
    private WebAgent<T> agent;
    private Function<T, R> parserFunction;

    @Override
    public CrawlTask<T, R> webAgent(WebAgent<T> agent) {
        this.agent = agent;
        return this;
    }

    @Override
    public CrawlTask<T, R> analyze(Function<T, R> parserFunction) {
        this.parserFunction = parserFunction;
        return this;
    }

    @Override
    public R run() throws Exception {
        return run(null);
    }

    @Override
    public R run(Map<String, Object> map) throws Exception {
        Assert.notNull(agent, "agent对象不能为空！");
        Assert.notNull(parserFunction, "解析函数不能为空！");
        CompletableFuture<R> cf = CompletableFuture.supplyAsync(() -> {
            try {
                return agent.execute(map);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenApply(parserFunction);
        return cf.get();
    }
}
