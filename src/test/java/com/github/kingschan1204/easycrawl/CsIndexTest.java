package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.engine.FileEngine;
import com.github.kingschan1204.easycrawl.core.agent.engine.HtmlEngine;
import com.github.kingschan1204.easycrawl.helper.map.MapUtil;
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
    public void industry() throws Exception {
        String cookieUrl = "https://www.csindex.com.cn/en/indices/index-detail/000300#/dataService/industryClassification";
        String reqUrl = "https://www.csindex.com.cn/csindex-home/exportExcel/security-industry-search-excel/CH";
        String referer = "https://www.csindex.com.cn/en/indices/index-detail/000300";
        Map<String, String> cookies = new HtmlEngine().referer("https://www.csindex.com.cn").url(cookieUrl).dataPull(null).getCookies();

        File file = new FileEngine()
                .folder("C:\\temp\\")
                .url(reqUrl)
                .head(new MapUtil<String, String>().put("Content-Type", "application/json; charset=utf-8").getMap())
                .referer(referer)
                .cookie(cookies)
                .method(HttpEngine.Method.POST)
                .body("{\"searchInput\":\"\",\"pageNum\":1,\"pageSize\":10,\"sortField\":null,\"sortOrder\":null}")
                .dataPull(null);
        System.out.println(file.getName());
    }
}
