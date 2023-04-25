package com.github.kingschan1204.easycrawl.core.agent.interceptor.impl;

import com.github.kingschan1204.easycrawl.core.agent.AgentResult;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.interceptor.AfterInterceptor;
import com.github.kingschan1204.easycrawl.helper.http.ResponseAssertHelper;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
/**
 * @author kingschan
 */
@Slf4j
public class StatusPrintInterceptorImpl implements AfterInterceptor {

    @Override
    public AgentResult interceptor(Map<String, Object> data, WebAgent webAgent) {
        AgentResult result = webAgent.getResult();
        log.debug("ContentType : {}", result.getContentType());
        log.debug("编码 {} ",result.getCharset());
        log.debug("Headers : {}", result.getHeaders());
        log.debug("Cookies : {}", result.getCookies());
        log.debug("耗时 {} 毫秒",result.getTimeMillis());
        ResponseAssertHelper.of(result).infer();
        return result;
    }
}
