package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

@Slf4j
@DisplayName("中证")
public class CsIndexTest {

    @DisplayName("行业分类表下载")
    @Test
    public void industry() {
        String cookieUrl = "https://www.csindex.com.cn/en/indices/index-detail/000300#/dataService/industryClassification";
        String reqUrl = "https://www.csindex.com.cn/csindex-home/exportExcel/security-industry-search-excel/CH";
        String referer = "https://www.csindex.com.cn/en/indices/index-detail/000300";
        Map<String, String> cookies = WebAgent.defaultAgent().referer("https://www.csindex.com.cn").url(cookieUrl).execute(null).getResult().getCookies();

        File file = new EasyCrawl<File>()
                .webAgent(WebAgent.defaultAgent().folder("C:\\temp\\")
                        .url(reqUrl)
                        .head("Content-Type", "application/json; charset=utf-8")
                        .referer(referer)
                        .cookie(cookies)
                        .method(HttpRequestConfig.Method.POST)
                        .body("""
                                {"searchInput":"","pageNum":1,"pageSize":10,"sortField":null,"sortOrder":null}
                                """)
                )
                .analyze(WebAgent::getFile)
                .execute();
        log.info("文件上名：{} 文件大小：{} kb", file.getName(), file.length() / 1024);
    }
}
