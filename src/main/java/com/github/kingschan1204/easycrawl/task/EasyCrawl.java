package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.WebAgentNew;
import com.github.kingschan1204.easycrawl.helper.http.UrlHelper;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
@Slf4j
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

    /**
     *
     * @param map 运行参数
     * @param pageIndexKey 页码key
     * @param totalKey 总记录条数key
     * @param pageSize
     * @return
     */
    public List<R> executePage(Map<String, Object> map,String pageIndexKey , String totalKey , Integer pageSize ){
        List<R> list = Collections.synchronizedList(new ArrayList<>());
        WebAgentNew data = webAgent.execute(map);
        JsonHelper json =  data.getJson();
        int totalRows = json.get(totalKey, Integer.class);
        int totalPage = (totalRows + pageSize - 1) / pageSize;
        log.debug("共{}记录,每页展示{}条,共{}页", totalRows, pageSize, totalPage);

        List<CompletableFuture<R>> cfList = new ArrayList<>();
        Consumer<R> consumer = (r) -> {
            if (r instanceof Collection) {
                list.addAll((Collection<? extends R>) r);
            } else {
                list.add(r);
            }
        };
        cfList.add(CompletableFuture.supplyAsync(() -> data).thenApply(parserFunction));
        cfList.get(0).thenAccept(consumer);

        for (int i = 2; i <= totalPage; i++) {
            String url = new UrlHelper(webAgent.getConfig().getUrl()).set(pageIndexKey, String.valueOf(i)).getUrl();
            CompletableFuture<R> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    return webAgent.url(url).execute(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).thenApply(parserFunction);
            cf.thenAccept(consumer);
            cfList.add(cf);
        }
        CompletableFuture.allOf(cfList.toArray(new CompletableFuture[]{})).join();
        return list;
    }
}
