package com.github.kingschan1204.easycrawl.helper.http;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author kings.chan
 * 2021-7-20
 * url 工具类
 */
@AllArgsConstructor
public class UrlHelper {

    private String url;

    /**
     * 设置url 参数
     *
     * @param name
     * @param value
     * @return
     */
    public UrlHelper set(String name, String value) {
        String regex = String.format("%s=[\\w-]+", name);
        String replace = String.format("%s=%s", name, value);
        this.url = this.url.replaceAll(regex, replace);
        return this;
    }

    public Map<String, String> getAll() {
        String urlString = this.url.replaceAll("^.*\\?", "");
        String[] urlArgs = urlString.split("&");
        return Arrays.stream(urlArgs).map(s -> s.split("=")).collect(Collectors.toMap(s -> Arrays.asList(s).get(0), v -> Arrays.asList(v).size() > 1 ? Arrays.asList(v).get(1) : ""));
    }

    public String get(String key) {
        Map<String, String> map = getAll();
        return map.get(key);
    }

    public String getUrl() {
        return this.url;
    }
}
