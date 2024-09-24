package com.github.kingschan1204.easycrawl.core.agent.interceptor.impl;

import com.github.kingschan1204.easycrawl.core.agent.AgentResult;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.interceptor.AfterInterceptor;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author kingschan
 */
@Slf4j
public class TranscodingInterceptorImpl implements AfterInterceptor {
    @Override
    public AgentResult interceptor(Map<String, Object> data, WebAgent webAgent) {
        AgentResult result = webAgent.getResult();
        String charset = result.getCharset();
        String contentType = result.getContentType();
        //	<meta http-equiv="Content-Type" content="text/html; charset=gbk"/>
        // <meta charSet="utf-8"/>
        if (null == charset && contentType.matches("text/html.*")) {
            log.warn("未获取到编码信息,有可能中文乱码！尝试自动提取编码！");
            String text = RegexHelper.findFirst(result.getBody(), RegexHelper.REGEX_HTML_CHARSET);
            charset = RegexHelper.findFirst(text, "(?i)charSet(\\s+)?=.*\"").replaceAll("(?i)charSet|=|\"|\\s", "");
            if (!charset.isEmpty()) {
                log.debug("编码提取成功,将自动转码：{}", charset);
                result.setBody(transcoding(result.getBodyAsByes(), charset));
            } else {
                log.warn("自动提取编码失败！");
            }
        }
        return result;
    }

    /**
     * 转码
     *
     * @param charset 将byte数组按传入编码转码
     * @return getContent
     */
    String transcoding(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
