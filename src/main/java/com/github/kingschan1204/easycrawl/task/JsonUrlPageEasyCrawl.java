package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.engine.HtmlAgent;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.http.UrlHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
@Deprecated
@Slf4j
public class JsonUrlPageEasyCrawl<AgentResult, R extends List<?>> implements CrawlTask<AgentResult, R> {

    private WebAgent<AgentResult> agent;
    private Function<AgentResult, R> parserFunction;


    @Override
    public CrawlTask<AgentResult, R> webAgent(WebAgent<AgentResult> agent) {
        this.agent = agent;
        return this;
    }

    @Override
    public CrawlTask<AgentResult, R> analyze(Function<AgentResult, R> parserFunction) {
        this.parserFunction = parserFunction;
        return this;
    }

    @Override
    public R run() throws Exception {
        return null;
    }

    @Override
    public R run(Map<String, Object> map) throws Exception {
        String pageIndexKey = "";
        String totalKey = "";
        Integer pageSize = 100;
        List<Object> list = Collections.synchronizedList(new ArrayList<>());
        AgentResult data = (AgentResult) ((HtmlAgent) agent).execute(map);
        JsonHelper json =  JsonHelper.of(data);
        int totalRows = json.get(totalKey, Integer.class);
        int totalPage = (totalRows + pageSize - 1) / pageSize;
        log.debug("共{}记录,每页展示{}条,共{}页", totalRows, pageSize, totalPage);

        List<CompletableFuture<R>> cfList = new ArrayList<>();
        Consumer<R> consumer = list::addAll;
        cfList.add(CompletableFuture.supplyAsync(() -> data).thenApply(parserFunction));
        cfList.get(0).thenAccept(consumer);

        for (int i = 2; i <= totalPage; i++) {
            String url = new UrlHelper(((HttpRequestConfig) agent).getUrl()).set(pageIndexKey, String.valueOf(i)).getUrl();
            CompletableFuture<R> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    return (AgentResult) ((HtmlAgent) agent.url(url)).execute(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).thenApply(parserFunction);
            cf.thenAccept(consumer);
            cfList.add(cf);
        }
        CompletableFuture.allOf(cfList.toArray(new CompletableFuture[]{})).join();
        return (R) list;
    }
}
