package com.github.kingschan1204.easycrawl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.engine.HtmlAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.AgentResult;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.map.MapUtil;
import com.github.kingschan1204.easycrawl.task.JsonApiPaginationTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@DisplayName("雪球测试")
public class XueQiuTest {


    @DisplayName("所有代码")
    @Test
    public void getAllCode() throws Exception {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page=1&size=200&only_count=0&current=&pct=&mc=&volume=&_=${timestamp}";
        WebAgent<AgentResult> engine = new HtmlAgent().url(apiurl).referer(referer);
        List<JSONObject> list = new JsonApiPaginationTask<JSONObject, List<JSONObject>>(engine, "page", "data.count", 200)
                .execute(r -> {
                    List<JSONObject> jsonObjects = new ArrayList<>();
                    JSONArray jsonArray = new JsonHelper(r).getObject("data.list", JSONArray.class);
                    for (int j = 0; j < jsonArray.size(); j++) {
                        jsonObjects.add(jsonArray.getJSONObject(j));
                    }
                    return jsonObjects;
                });
        log.info("共{}支股票",list.size());
        list.forEach(System.out::println);

    }


    @DisplayName("历史分红")
    @Test
    public void getBonus() throws Exception {
        String page = "https://xueqiu.com";
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/bonus.json?symbol=SH600519&size=100&page=1&extend=true";
        String referer = "https://xueqiu.com/snowman/S/${code}/detail";
        Map<String, String> cookies = new HtmlAgent().url(page).execute(new MapUtil<String,Object>().put("code","SH600519").getMap()).getCookies();
        String data = new HtmlAgent().url(apiUrl).referer(referer).cookie(cookies).execute(null).getBody();
        System.out.println(data);
    }

    @DisplayName("公司简介")
    @Test
    public void companyInfo() throws Exception {
        String page = "https://xueqiu.com";
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/company.json?symbol=${code}";
        String data = new HtmlAgent()
                .url(apiUrl)
                .referer(page)
                .cookie(new HtmlAgent().url(page).execute(null).getCookies())
                .execute(new MapUtil<String,Object>().put("code","SH600887").getMap())
                .getBody();
        System.out.println(data);
    }

    @DisplayName("top10 股东")
    @Test
    public void top10() throws Exception {
        String page = "https://xueqiu.com";
        Map<String,Object> map = new MapUtil<String,Object>().put("code","SH600887").getMap();
        Map<String,String> cookies = new HtmlAgent().url(page).execute(null).getCookies();
        //获取最新的十大股东 及 所有时间列表
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=${code}&circula=0&count=200";
        String data = new HtmlAgent()
                .url(apiUrl)
                .referer(page)
                .cookie(cookies)
                .execute(map)
                .getBody();
        System.out.println(data);
        //指定具体时间获取top10
        String reportUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=${code}&locate=1669824000000&start=1669824000000&circula=0";
        data = new HtmlAgent()
                .url(reportUrl)
                .referer(page)
                .cookie(cookies)
                .execute(map)
                .getBody();
        System.out.println(data);
    }

    @DisplayName("股东人数")
    @Test
    public void gdrs() throws Exception {
        String page = "https://xueqiu.com/snowman/S/${code}/detail#/GDRS";
        Map<String,Object> map = new MapUtil<String,Object>().put("code","SH600887").getMap();
        Map<String,String> cookies = new HtmlAgent().url(page).execute(map).getCookies();
        //获取最新的十大股东 及 所有时间列表
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/holders.json?symbol=${code}&extend=true&page=1&size=100";
        String data = new HtmlAgent()
                .url(apiUrl)
                .referer(page)
                .cookie(cookies)
                .execute(map)
                .getBody();
        System.out.println(data);
    }


}
