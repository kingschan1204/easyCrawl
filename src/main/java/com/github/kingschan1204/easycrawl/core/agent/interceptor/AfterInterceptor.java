package com.github.kingschan1204.easycrawl.core.agent.interceptor;

import com.github.kingschan1204.easycrawl.core.agent.AgentResult;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;

import java.util.Map;

/**
 * @author kingschan
 */
public interface AfterInterceptor {

    AgentResult interceptor(Map<String, Object> data, WebAgent webAgent);

}
