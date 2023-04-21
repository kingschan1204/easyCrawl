package com.github.kingschan1204.easycrawl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.collections.MapUtil;
import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.sql.SqlHelper;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@DisplayName("雪球测试")
public class XueQiuTest {
    String page = "https://xueqiu.com";

    @DisplayName("所有代码")
    @Test
    public void getAllCode() {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page=1&size=200&only_count=0&current=&pct=&mc=&volume=&_=${timestamp}";
        List list = new EasyCrawl<List<JSONObject>>()
                .webAgent(WebAgent.defaultAgent().url(apiurl).referer(referer))
                .analyze(r -> {
                    List<JSONObject> jsonObjects = new ArrayList<>();
                    JSONArray jsonArray = r.getJson().get("data.list", JSONArray.class);
                    for (int j = 0; j < jsonArray.size(); j++) {
                        jsonObjects.add(jsonArray.getJSONObject(j));
                    }
                    return jsonObjects;
                }).executePage(null, "page", "data.count", 200);

        log.info("共{}支股票", list.size());
        list.forEach(System.out::println);

    }


    @DisplayName("历史分红")
    @Test
    public void getBonus() {
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/bonus.json?symbol=${code}&size=100&page=1&extend=true";
        String referer = "https://xueqiu.com/snowman/S/SH600887/detail";
        Map<String, Object> args = new MapUtil<String, Object>().put("code", "SH600887").getMap();
        Map<String, String> cookies = getXQCookies();

        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(referer).cookie(cookies))
                .analyze(WebAgent::getText)
                .execute(args);
        System.out.println(data);
        JSONArray rows = JsonHelper.of(data).get("data.items", JSONArray.class);
        StringBuffer sqls = new StringBuffer();
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            //把时间戳转为可读日期
            for (String key : row.keySet()) {
                if (row.get(key) instanceof Long) {
                    row.put(key, DateHelper.of(row.getLong(key)).date());

                }
            }
            String insert = SqlHelper.insert(row.keySet().toArray(new String[]{}), row.values().toArray(new Object[]{}), "dividend");
            sqls.append(insert);

        }
        System.out.println(sqls);

    }

    @DisplayName("公司简介")
    @Test
    public void companyInfo() {

        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/company.json?symbol=${code}";
        String data =  new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(page).cookie(getXQCookies()))
                .analyze(WebAgent::getText)
                .execute(new MapUtil<String, Object>().put("code", "SH600887").getMap());
        System.out.println(data);
    }

    @DisplayName("top10 股东")
    @Test
    public void top10() {
        Map<String, Object> map = new MapUtil<String, Object>().put("code", "SH600887").getMap();
        Map<String, String> cookies = getXQCookies();
        //获取最新的十大股东 及 所有时间列表
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=${code}&circula=0&count=200";
        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(page).cookie(WebAgent.getCookies(page)))
                .analyze(WebAgent::getText)
                .execute(map);
        System.out.println(data);
        //指定具体时间获取top10
        String reportUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=${code}&locate=1669824000000&start=1669824000000&circula=0";
        data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(reportUrl).referer(page).cookie(cookies))
                .analyze(WebAgent::getText)
                .execute(map);
        System.out.println(data);
    }

    @DisplayName("股东人数")
    @Test
    public void gdrs() {
        String page = "https://xueqiu.com/snowman/S/${code}/detail#/GDRS";
        Map<String, Object> map = new MapUtil<String, Object>().put("code", "SH600887").getMap();
        Map<String, String> cookies = getXQCookies();
        //获取最新的十大股东 及 所有时间列表
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/holders.json?symbol=${code}&extend=true&page=1&size=100";
        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(page).cookie(cookies))
                .analyze(WebAgent::getText)
                .execute(map);
        System.out.println(data);
    }


    @DisplayName("日k线")
    @Test
    public void dayOfKline() {
        int pageSize = -284;
        Map<String, Object> map = new MapUtil<String, Object>()
                .put("code", "SH600887") //股票代码
                .put("begin", System.currentTimeMillis()) //开始时间
                .put("count", pageSize) //查过去多少天的数据
                .getMap();
        Map<String, String> cookies = getXQCookies();

        //获取最新的十大股东 及 所有时间列表
        String referer = "https://xueqiu.com/S/${code}";
        String dataUrl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=${code}&begin=${begin}&period=day&type=before&count=${count}&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance";
        List<String> list = new ArrayList<>();
        boolean hasNext = true;
        while (hasNext) {
            JsonHelper jsonHelper = new EasyCrawl<JsonHelper>()
                    .webAgent(WebAgent.defaultAgent().url(dataUrl).referer(referer).cookie(cookies))
                    .analyze(WebAgent::getJson).execute(map);

            JSONArray columns = jsonHelper.get("data.column", JSONArray.class);
            JSONArray rows = jsonHelper.get("data.item", JSONArray.class);
            StringBuffer sqls = new StringBuffer();
            for (int i = 0; i < rows.size(); i++) {
                JSONArray array = rows.getJSONArray(i);
                //2000-01-01 00:00:00
                if (DateHelper.of(array.getLong(0)).year().equals("2000")) {
                    System.out.println(String.format("2000年以前的数据不要了！%s", DateHelper.of(array.getLong(0)).date()));
                    hasNext = false;
                    break;
                }
                //把时间戳转为可读日期
                array.set(0, DateHelper.of(array.getLong(0)).date());
                String insert = SqlHelper.insert(columns.toArray(new String[]{}), array.toArray(new Object[]{}), "kline_600887");
                sqls.append(insert);

            }
            list.add(sqls.toString());
            //设置下一次的参数
            System.out.println(
                    String.format("时间范围： %s ~ %s",
                            DateHelper.of(rows.getJSONArray(0).getLong(0)).date(),
                            DateHelper.of(rows.getJSONArray(rows.size() - 1).getLong(0)).date())
            );
            map.put("begin", rows.getJSONArray(0).getLong(0));
            if (rows.size() < Math.abs(pageSize)) {
                System.out.println("没有数据了！");
                hasNext = false;
                break;
            }
        }
        list.forEach(System.err::println);

    }

    @DisplayName("个股详情")
    @Test
    public void proxyTest() {
        Map<String, String> cookies = getXQCookies();
        String apiUrl = "https://stock.xueqiu.com/v5/stock/quote.json?symbol=SH600887&extend=detail";
        String referer = "https://xueqiu.com/S/SH600887";
        String result = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().referer(referer).cookie(cookies).url(apiUrl))
                .analyze(r -> r.getResult().getBody()).execute();
        System.out.println(result);
    }

    @DisplayName("主要指标")
    @Test
    public void mainIndex() throws Exception {
        String apiUrl = "https://stock.xueqiu.com/v5/stock/finance/cn/indicator.json?symbol=SZ002304&type=Q4&is_detail=true&count=5&timestamp=";
        String referer = "https://xueqiu.com/snowman/S/SZ002304/detail";
        JsonHelper result = new EasyCrawl<JsonHelper>()
                .webAgent(WebAgent.defaultAgent().referer(referer).cookie(getXQCookies()).url(apiUrl))
                .analyze(WebAgent::getJson)
                .execute();
        System.out.println(result);
    }

    Map<String, String> getXQCookies(){
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }
}
