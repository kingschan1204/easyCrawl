package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;

import java.util.function.Function;

public interface CrawlTask<T,R> {

    CrawlTask<T,R> webAgent(WebAgent<T> agent);

//    analyze(Function<>)

}
