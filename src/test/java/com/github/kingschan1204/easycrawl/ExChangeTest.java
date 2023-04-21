package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("大盘指数测试")
public class ExChangeTest {

    @DisplayName("新浪")
    @Test
    public void sina() throws Exception {
        String referer = "http://finance.sina.com.cn";
        String apiUrl = "http://hq.sinajs.cn/list=sz399001,sh000001,sz399006,sh000300";
        String data =  new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().referer(referer).url(apiUrl))
                .analyze(WebAgent::getText)
                .execute();
        System.out.println(data);
    }
}
