package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;

import java.util.Map;
import java.util.function.Function;

/**
 *
 * @param <T> agent返回类型
 * @param <R> 解析加工成的类型
 */
public interface CrawlTask<T,R> {

    CrawlTask<T,R> webAgent(WebAgent<T> agent);

    CrawlTask<T,R> analyze(Function<T,R> parserFunction);

    R run() throws Exception;
    R run(Map<String,Object> map) throws Exception;

}
