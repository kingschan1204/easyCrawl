package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.collections.MapUtil;
import com.github.kingschan1204.easycrawl.helper.http.CURLHelper;
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
        result.op("data.list").forEach(r -> {
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

    @DisplayName("深圳成指日K")
    @Test
    public void kline() {
        String curl = """
                curl 'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SZ399001&begin=1689177600000&period=day&type=before&count=-142&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance' \\
                  -H 'accept: application/json, text/plain, */*' \\
                  -H 'accept-language: zh-CN,zh;q=0.9' \\
                  -H 'cache-control: no-cache' \\
                  -H 'cookie: cookiesu=411705905647265; xq_is_login=1; u=9920891343; device_id=b0919ad20422f64df3c087abdc99ffd1; s=c4152uhran; bid=398f9fc77230b7b693c8a9c3fe026e8b_lwybl995; xq_a_token=6d9d1bb88081538ed78765eb466f28f0fa64830d; xqat=6d9d1bb88081538ed78765eb466f28f0fa64830d; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOjk5MjA4OTEzNDMsImlzcyI6InVjIiwiZXhwIjoxNzI3NDAzNTI1LCJjdG0iOjE3MjQ4MTE1MjUzNDYsImNpZCI6ImQ5ZDBuNEFadXAifQ.gX_yQaW-KC4ETzvbjxKVIvSDXeKKdW-iIuJgJcDUDKY4QZsOr8c09vOnmRmgHww_Dz6rjY_Jp8KJ5tpvs2xeSwyYgC-8pV51ynFmssn34XiBFfV0B_3QDCb6kTK0p79PeK3R8TBkx6mSk2Mvz_2KzSmN_VrkiNsUT4WfgDIiOGTTAYZ4oE3NBbmPU6EORfopUioHWcuiV0KU5JehwMDcVUorCRBOaOtIBx9hRS4CLR8LlBpGA0CwzcD0wcf_eD3kVBawTgOSQN5Jq6f_MVVZ7EEH_jA3B0RfiP0Aq6LxUDXJJkM7-DRzvZ5tT7VbCcac6vudKijWIRIZD3GBBGn5tQ; xq_r_token=2320c377342e69335e692a2615b58f52fcdeffac; Hm_lvt_1db88642e346389874251b5a1eded6e3=1725415797,1725498980,1725586719,1725929319; HMACCOUNT=4FCC4C845FF2961F; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1725929367; ssxmod_itna=QqAx9D2D0DcDBGDyADzxmxB+4kEbdqitFhqKrYY1YqFqzDlh0ixA5D8D6DQeGTWgYBA+PPG80DKqPXiiGG47hWecoW7rYe4Fn3YADB3DEx06FzQixiiZDCeDIDWeDiDG+8D=xGYDjQNXzuxGWDeKDmxiODlKOcxDaBxi3ucKDRrYD01w7DQKDuOv4DG5aWhGxqB0mSoFpYRoDn=GzEQ5D9goDs2iU3Q5ISRL4/EyjEeoQ6EA+DCKDjhBCDmeOC+h1DKoPqePyWimqWmedYBiYiO4xkL0QEi0ehiroQQhoFe/HgV=Di6i/s=DxD==; ssxmod_itna2=QqAx9D2D0DcDBGDyADzxmxB+4kEbdqitFhqKrYY1YqFqD6Z9bD05wrY03K3egYQIe2D=D6722UC6Q+Cgb+PT8OOYwYREBvPP=E+6ium85QAoFsqSFerRPgYj9vXGnAIIx=j8p7PM2pSRGC38iIWh0mKDxMKm6YFRQeCl=InE0DcMNY5YXdgn4MYCDBS8NBj+n+7A3kYn0U7a3AAoo9AfQZmc4quTa17O=zoQppRw=kgnj+lOa8Ot2p4cgkHfK9+IoAOqjYQbastg3421SV0bP5AhXQgW88WTEPEo4bx6F63vKqKmjiddddhrjbdh03Dbd+B3/DHLEDgAhKo4SGQHuxWEY3uYQid7bktxiuLYWQ5h0kPraA238hNQibDdbMTdEEKj7i8CxyoIQBdXkQVA=wkI5u4WxHduDj4QLoeR4QQ8aH+aqi1u+Nq7peM5eGre05N1sS7fQ3eGg1rBL4mK0QeLhGO+OE1Dvj6e+TFewSi=K2DoqUPqij0Q5YG53zj07S24sPHqQoT6d/idKDEPz8+0RdjwKRCeYpK33xh7mEd1e1UGKeDbCUFq4rQ2xibXh3tiQTbz55kb9FmtYsXMFkb6wnE34fY48E0DXDDwhD4Wv+rDdDomBwRfvOtrBQHY4xoAECehSxvkYicZKas5XIqtKf+gD4AwqDKbmZ95+mD1Y=ZlDs5QY50WA8yyqQ5j5o0YPTYiDS58ZqIWxt10xHxrAiBgklyeDr3D4DDLxD+O36GxWqppDD==' \\
                  -H 'dnt: 1' \\
                  -H 'origin: https://xueqiu.com' \\
                  -H 'pragma: no-cache' \\
                  -H 'priority: u=1, i' \\
                  -H 'referer: https://xueqiu.com/S/SZ399001' \\
                  -H 'sec-ch-ua: "Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"' \\
                  -H 'sec-ch-ua-mobile: ?0' \\
                  -H 'sec-ch-ua-platform: "Windows"' \\
                  -H 'sec-fetch-dest: empty' \\
                  -H 'sec-fetch-mode: cors' \\
                  -H 'sec-fetch-site: same-site' \\
                  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36'
                """;
        HttpRequestConfig config = new CURLHelper(curl).getConfig();
        System.out.println(JsonHelper.of(config).pretty());
        JsonHelper result = new EasyCrawl<JsonHelper>()
                .webAgent(WebAgent.defaultAgent(config))
                .analyze(WebAgent::getJson)
                .execute();
        System.out.println(result);
    }

    Map<String, String> getXQCookies() {
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }
}
