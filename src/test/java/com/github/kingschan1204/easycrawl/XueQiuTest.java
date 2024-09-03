package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.collections.MapUtil;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.math.MathHelper;
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


    @DisplayName("历史分红")
    @Test
    public void getBonus() {
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/bonus.json?symbol=${code}&size=100&page=1&extend=true";
        String referer = "https://xueqiu.com/snowman/S/SH600887/detail";
        Map<String, String> cookies = getXQCookies();

        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(referer).cookie(cookies))
                .analyze(WebAgent::getText)
                .args("code", "SH600887")
                .execute();
        System.out.println(data);


    }

    @DisplayName("公司简介")
    @Test
    public void companyInfo() {

        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/company.json?symbol=${code}";
        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(page).cookie(getXQCookies()))
                .analyze(WebAgent::getText)
                .args("code", "SH600887")
                .execute();
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
                .args(map)
                .execute();
        System.out.println(data);
        //指定具体时间获取top10
        String reportUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/top_holders.json?symbol=${code}&locate=1669824000000&start=1669824000000&circula=0";
        data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(reportUrl).referer(page).cookie(cookies))
                .analyze(WebAgent::getText)
                .args(map)
                .execute();
        System.out.println(data);
    }

    @DisplayName("股东人数")
    @Test
    public void gdrs() {
        String page = "https://xueqiu.com/snowman/S/${code}/detail#/GDRS";
        Map<String, String> cookies = getXQCookies();
        //获取最新的十大股东 及 所有时间列表
        String apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/holders.json?symbol=${code}&extend=true&page=1&size=100";
        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(page).cookie(cookies))
                .analyze(WebAgent::getText)
                .args("code", "SH600887")
                .execute();
        System.out.println(data);
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

    @DisplayName("资产负债表")
    @Test
    public void balanceSheet() throws Exception {
        //type=Q4 年报
        //type=all 全部
        //type=Q2 中报
        //type=Q3 三季报
        //type=Q1 一季报
        String apiUrl = "https://stock.xueqiu.com/v5/stock/finance/cn/balance.json?symbol=SZ002304&type=all&is_detail=true&count=50&timestamp=";
        String referer = "https://xueqiu.com/snowman/S/SZ002304/detail";
        JsonHelper result = new EasyCrawl<JsonHelper>()
                .webAgent(WebAgent.defaultAgent().referer(referer).cookie(getXQCookies()).url(apiUrl))
                .analyze(WebAgent::getJson)
                .execute();
        // inventory 存货
        // contract_liabilities 合同负债
//        System.out.println(result);
        result.op("data.list").forEach(r ->{
            String rowText = String.format("%s 存货:%s 合同负债:%s",
                    r.get("report_name").asText(),
                    MathHelper.of(r.get("inventory").get(0).decimalValue()).pretty(),
                    MathHelper.of(r.get("contract_liabilities").get(0).decimalValue()).pretty()
            );
            System.out.println(rowText);
        });
    }


    @DisplayName("所有代码")
    @Test
    public void getAllCode() {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page=1&size=200&only_count=0&current=&pct=&mc=&volume=&_=${timestamp}";
        List list = new EasyCrawl<List<Map<String, Object>>>()
                .webAgent(WebAgent.defaultAgent().url(apiurl).referer(referer))
                .analyze(r -> {
                    List<Map<String, Object>> result = new ArrayList<>();
                    r.getJson().op("data.list").forEach(row -> {
                        result.add(
                                new MapUtil<String, Object>()
                                        .put(
                                                row.get("name").asText(),
                                                row.get("symbol").asText()
                                        ).getMap()
                        );
                    });
                    return result;
                }).executePage(null, "page", "data.count", 200);

        log.info("共{}支股票", list.size());
        list.forEach(System.out::println);

    }

    Map<String, String> getXQCookies() {
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }
}
