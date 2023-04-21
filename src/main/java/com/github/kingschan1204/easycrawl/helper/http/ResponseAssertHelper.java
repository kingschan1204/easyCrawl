package com.github.kingschan1204.easycrawl.helper.http;

import com.github.kingschan1204.easycrawl.core.agent.AgentResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseAssertHelper {

    private AgentResult agentResult;

    public ResponseAssertHelper(AgentResult agentResult) {
        this.agentResult = agentResult;
    }

    public static ResponseAssertHelper of(AgentResult agentResult) {
        return new ResponseAssertHelper(agentResult);
    }

    public void infer() {
        String type = agentResult.getContentType();
        String content = "不知道是个啥！";
        if (type.matches("text/html.*")) {
            content = "html";
        } else if (type.matches("application/json.*")) {
            content = "json";
        } else if (type.matches("application/vnd.ms-excel.*")) {
            content = "excel";
        } else if (type.matches("text/css.*")) {
            content = "css";
        } else if (type.matches("application/javascript.*")) {
            content = "js";
        }else if (type.matches("image.*")) {
            content = "图片";
        }else if (type.matches("application/pdf.*")) {
            content = "pdf";
        }
        log.debug("推测 http 响应类型：{}", content);
    }

}
