package com.github.kingschan1204.easycrawl.core.agent;


import com.github.kingschan1204.easycrawl.core.agent.utils.AgentResult;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;

import java.io.File;
import java.net.Proxy;
import java.util.Map;

/**
 * @author kings.chan
 * 2023-04-21
 **/

public interface WebAgentNew {

//    enum ResponseType {
//        FILE(File.class), HTML(String.class), JSON(JsonHelper.class), TEXT(String.class);
//        public Class<?> type;
//
//        ResponseType(Class<?> clazz) {
//            type = clazz;
//        }
//    }

//    WebAgentNew of(HttpRequestConfig config);

    /**
     * 默认agent
     *
     * @return GenericHttp1Agent
     */
    static WebAgentNew defaultAgent() {
        return new GenericHttp1Agent();
    }

    static Map<String, String> getCookies(String url) {
        return defaultAgent().url(url).execute(null).getResult().getCookies();
    }

    HttpRequestConfig getConfig();

    WebAgentNew url(String url);

    WebAgentNew referer(String referer);

    WebAgentNew method(HttpRequestConfig.Method method);

    WebAgentNew head(Map<String, String> head);

    WebAgentNew useAgent(String useAgent);

    WebAgentNew cookie(Map<String, String> cookie);

    WebAgentNew timeOut(Integer timeOut);

    WebAgentNew proxy(Proxy proxy);

    WebAgentNew body(String body);

    WebAgentNew folder(String folder);

    WebAgentNew fileName(String fileName);

    WebAgentNew execute(Map<String, Object> data);

    AgentResult getResult();

    JsonHelper getJson();

    String getText();

    File getFile();

}
