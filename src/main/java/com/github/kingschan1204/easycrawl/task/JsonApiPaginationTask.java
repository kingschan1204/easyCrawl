package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.AgentResult;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.url.UrlHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class JsonApiPaginationTask<T, R> {

    WebAgent<AgentResult> agent;
    String pageIndexKey;
    String totalKey;
    Integer pageSize;

    public List<T> execute(Function<String, R> parserFunction) throws Exception {
        List<T> list = Collections.synchronizedList(new ArrayList<>());
        String data = agent.execute(null).getBody();
        JsonHelper json = new JsonHelper(data);
        int totalRows = json.getObject(totalKey, Integer.class);
        int totalPage = (totalRows + pageSize - 1) / pageSize;
        log.debug("共{}记录,每页展示{}条,共{}页", totalRows, pageSize, totalPage);

        List<CompletableFuture<R>> cfList = new ArrayList<>();
        Consumer<R> consumer = (r) -> {
            if (r instanceof Collection) {
                list.addAll((Collection<? extends T>) r);
            } else {
                list.add((T) r);
            }
        };
        cfList.add(CompletableFuture.supplyAsync(() -> data).thenApply(parserFunction));
        cfList.get(0).thenAccept(consumer);

        for (int i = 2; i <= totalPage; i++) {
            String url = new UrlHelper(((HttpEngine) agent).getUrl()).set(pageIndexKey, String.valueOf(i)).getUrl();
            CompletableFuture<R> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    return agent.url(url).execute(null).getBody();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }).thenApply(parserFunction);
            cf.thenAccept(consumer);
            cfList.add(cf);
        }
        CompletableFuture.allOf(cfList.toArray(new CompletableFuture[]{})).join();
        return list;
    }


}
