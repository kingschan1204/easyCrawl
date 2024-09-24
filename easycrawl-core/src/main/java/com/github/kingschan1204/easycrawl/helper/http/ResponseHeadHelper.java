package com.github.kingschan1204.easycrawl.helper.http;

import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.util.Map;

/**
 * http响应头工具类
 *
 * @author kings.chan
 * 2023-4-21
 */
@Slf4j
public class ResponseHeadHelper {
    /**
     * HTTP响应中发送的数据如何被显示或保存。它通常用于控制浏览器如何处理HTTP响应的附件，以及在下载文件时提示用户文件的名称和类型等信息。
     * Content-disposition: <type>; filename="<filename>"
     */
    public static final String CONTENT_DISPOSITION = "Content-disposition";

    private final Map<String, String> responseHeaders;

    public ResponseHeadHelper(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public static ResponseHeadHelper of(Map<String, String> responseHeaders) {
        return new ResponseHeadHelper(responseHeaders);
    }

    /**
     * 从Content-disposition头里获取文件名
     * @return
     */
    public String getFileName() {
        String fileName;
        try {
            String key = responseHeaders.keySet().stream().filter(r -> r.matches("(?i)Content-disposition")).findFirst().get();
            String cd = responseHeaders.get(key);
            //有可能要转码
            String decode = URLDecoder.decode(cd, "UTF-8");
            fileName = decode.replaceAll(".*=", "");
            log.debug("提取到的文件名 : {}",fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return fileName;
    }

    /**
     * 是否是文件下载类型的响应头
     *
     * @return true or false
     */
    public boolean fileContent() {
        return responseHeaders.keySet().stream().map(String::toLowerCase).anyMatch(r -> r.equals(CONTENT_DISPOSITION.toLowerCase()));
    }


}
