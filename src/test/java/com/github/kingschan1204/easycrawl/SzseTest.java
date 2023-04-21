package com.github.kingschan1204.easycrawl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.http.UrlHelper;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
@DisplayName("深交所测试")
public class SzseTest {

    String referer = "http://www.szse.cn/disclosure/index.html";
    String useAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    String apiUrl = "http://www.szse.cn/api/report/exchange/onepersistenthour/monthList?month=2023-03";


    TreeMap<String, Boolean> getDay(String month) {
        try {
            String url = new UrlHelper(apiUrl).set("month", month).getUrl();
           String data = new EasyCrawl<String>()
                    .webAgent(WebAgent.defaultAgent().referer(referer).url(url))
                    .analyze(WebAgent::getText)
                    .execute();
            return parserData(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    TreeMap<String, Boolean> parserData(String text) {
        JSONObject json = new JSONObject(true);
        json = JSON.parseObject(text);
        JSONArray jsonArray = json.getJSONArray("data");
        TreeMap<String, Boolean> data = new TreeMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            data.put(
                    jsonArray.getJSONObject(i).getString("jyrq"),
                    jsonArray.getJSONObject(i).getInteger("jybz") == 1
            );
        }
        return data;
    }

    @DisplayName("交易日数据")
    @Test
    public void tradingDay() throws Exception {

        int year = 2020;
        int end = 2023;
        List<String> month = new ArrayList<>();
        for (int i = year; i <= end; i++) {
            for (int j = 1; j < 13; j++) {
                month.add(String.format("%s-%02d", i, j));
            }
        }
        List<CompletableFuture> list = new ArrayList<>();
        for (String s : month) {
            CompletableFuture cf = CompletableFuture.supplyAsync(() -> getDay(s));
            cf.thenAccept(System.out::println);
            list.add(cf);
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[]{})).join();
    }

    @DisplayName("月份交易日数据")
    @Test
    public void proxyTest() throws Exception {
        String month = "2023-03";
        String url = new UrlHelper(apiUrl).set("month", month).getUrl();
        TreeMap<String, Boolean> result = new EasyCrawl< TreeMap<String, Boolean>>()
                .webAgent(WebAgent.defaultAgent().referer(url).useAgent(useAgent).url(apiUrl))
                .analyze(r -> parserData(r.getResult().getBody()))
                .execute();
        System.out.println(result);
    }

}
