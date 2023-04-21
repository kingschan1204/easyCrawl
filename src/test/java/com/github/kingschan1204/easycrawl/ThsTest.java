package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@DisplayName("同花顺测试")
public class ThsTest {

    String url = "http://basic.10jqka.com.cn/api/stock/export.php?export=main&type=year&code=600519";
    String referer = "http://basic.10jqka.com.cn/api/stock/export.php?export=main&type=year&code=600519";

    @DisplayName("年报下载")
    @Test
    public void getYearReport() {
        File file = new EasyCrawl<File>()
                .webAgent(WebAgent.defaultAgent().folder("C:\\temp\\")
                        .referer(referer)
                        .url(url))
                .analyze(WebAgent::getFile)
                .execute();
        System.out.println(String.format("文件上名：%s 文件大小：%s kb", file.getName(), file.length() / 1024));
    }

    @DisplayName("公司资料")
    @Test
    public void company() {
        Map<String, String> map = new EasyCrawl<Map<String, String>>()
                .webAgent(WebAgent.defaultAgent().referer("http://basic.10jqka.com.cn").url("https://basic.10jqka.com.cn/600519/company.html"))
                .analyze(r -> {
                    Map<String, String> m = new HashMap<>();
                    String content = r.getText();
                    Document doc = Jsoup.parse(content);
                    Elements elements = doc.select("#detail > div.bd > table > tbody > tr.video-btn-box-tr > td:nth-child(2) > span");
                    m.put("name", elements.text());
                    m.put("control", doc.select("#detail > div.bd > div > table > tbody > tr:nth-child(4) > td > div > span").text());
                    m.put("url", doc.select("#detail > div.bd > table > tbody > tr:nth-child(3) > td:nth-child(2) > span").text());
                    return m;
                }).execute();
        System.out.println(map);
    }


}
