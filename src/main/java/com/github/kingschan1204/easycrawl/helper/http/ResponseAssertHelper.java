package com.github.kingschan1204.easycrawl.helper.http;

import com.github.kingschan1204.easycrawl.core.agent.AgentResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kingschan
 */
@Slf4j
public class ResponseAssertHelper {

    private AgentResult result;

    public ResponseAssertHelper(AgentResult agentResult) {
        this.result = agentResult;
    }

    public static ResponseAssertHelper of(AgentResult agentResult) {
        return new ResponseAssertHelper(agentResult);
    }

    public void infer() {
        statusCode();
        contentType();
    }

    public void statusCode() {
        log.debug("http状态：{}", result.getStatusCode());
        if (!result.getStatusCode().equals(200)) {
            if (result.getStatusCode() >= 500) {
                log.warn("服务器错误！");
            } else if (result.getStatusCode() == 404) {
                log.warn("地址不存在！");
            } else if (result.getStatusCode() == 401) {
                log.warn("401表示未经授权！或者证明登录信息的cookie,token已过期！");
            } else if (result.getStatusCode() == 400) {
                log.warn("传参有问题!一般参数传少了或者不正确！");
            }else{
                log.warn("未支持的状态码: {}",result.getStatusCode());
            }
        }
    }

    public void contentType() {
        String type = result.getContentType();
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
        } else if (type.matches("image.*")) {
            content = "图片";
        } else if (type.matches("application/pdf.*")) {
            content = "pdf";
        }
        log.debug("推测 http 响应类型：{}", content);
    }

}
