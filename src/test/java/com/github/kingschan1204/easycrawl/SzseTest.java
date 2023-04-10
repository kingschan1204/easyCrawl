package com.github.kingschan1204.easycrawl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kingschan1204.easycrawl.core.agent.engine.HtmlAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.AgentResult;
import com.github.kingschan1204.easycrawl.helper.url.UrlHelper;
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
    String apiurl = "http://www.szse.cn/api/report/exchange/onepersistenthour/monthList?month=2023-03";


    TreeMap<String, Boolean> getDay(String month) {
        try {
            String url = new UrlHelper(apiurl).set("month", month).getUrl();
            AgentResult webPage = new HtmlAgent().url(url).useAgent(useAgent).referer(referer).timeOut(6000).execute(null);
            System.out.println(webPage.getBody());
            JSONObject json = new JSONObject(true);
            json = JSON.parseObject(webPage.getBody());
            JSONArray jsonArray = json.getJSONArray("data");
            TreeMap<String, Boolean> data = new TreeMap<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                data.put(
                        jsonArray.getJSONObject(i).getString("jyrq"),
                        jsonArray.getJSONObject(i).getInteger("jybz") == 1
                );
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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


}
