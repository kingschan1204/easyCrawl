package crawl.restapi;

import com.github.kingschan1204.easycrawl.core.agent.result.HttpResult;
import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import com.github.kingschan1204.easycrawl.task.ThinEasyCrawl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("xueqiu")
public class XqTest {
    Map<String, String> cookies;

    @BeforeEach
    public void initCookies() {
        if (cookies == null) {
            String curl = "https://www.xueqiu.com/about";
            cookies = new ThinEasyCrawl(curl).execute().cookies();
        }
    }

    @DisplayName("allCodes")
    @Test
    public void allCodes() {
        String curl =
                """
                        curl 'https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page=1&size=200&only_count=0&current=&pct=&mc=&volume=&_=1732626100567&md5__1038=n4%2BxciD%3D0QK7T4mqBKDsD7fmg4YwMwBSSxD5%3Dx' \\
                          -H 'Accept: application/json, text/javascript, */*; q=0.01' \\
                          -H 'Accept-Language: zh-CN,zh;q=0.9' \\
                          -H 'Cache-Control: no-cache' \\
                          -H 'Connection: keep-alive' \\
                          -H 'DNT: 1' \\
                          -H 'Pragma: no-cache' \\
                          -H 'Referer: https://xueqiu.com/stock/screener?market=undefined&first_name=5&second_name=0' \\
                          -H 'Sec-Fetch-Dest: empty' \\
                          -H 'Sec-Fetch-Mode: cors' \\
                          -H 'Sec-Fetch-Site: same-origin' \\
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36' \\
                          -H 'X-Requested-With: XMLHttpRequest' \\
                          -H 'sec-ch-ua: "Google Chrome";v="131", "Chromium";v="131", "Not_A Brand";v="24"' \\
                          -H 'sec-ch-ua-mobile: ?0' \\
                          -H 'sec-ch-ua-platform: "Windows"'
                        """;
        List result =
                new EasyCrawl<List<Map<String, Object>>>()
                        .webAgent(curl)
                        .analyze(r -> r.getResult().getJson().op("data.list").toListMap())
                        // xq这个url地址：注意pageSize最大只支持200
                        .executePage(null, "page", "data.count", 200);
            System.out.println(result);
        System.out.println(result.size());
    }
    @DisplayName("industries")
    @Test
    public void industries(){
        //申万分类 https://www.swsresearch.com/institute_sw/allIndex/downloadCenter/industryType
        // 中证行业分类 https://www.csindex.com.cn/#/dataService/industryClassification
        String curl = """
                curl 'https://xueqiu.com/service/screener/industries?category=CN&_=1740406777921&md5__1038=WqIxBWqmwxyDlxGgx%2BExAoIx9nl3DOAfeD' \\
                            -H 'accept: application/json, text/javascript, */*; q=0.01' \\
                            -H 'accept-language: zh-CN,zh;q=0.9' \\
                            -H 'cache-control: no-cache' \\
                            -H 'dnt: 1' \\
                            -H 'priority: u=1, i' \\
                            -H 'referer: https://xueqiu.com/stock/screener?name=%E9%80%89%E8%82%A1%E5%B7%A5%E5%85%B7&url=https://xueqiu.com/stock/screener&first_name=5&second_name=0' \\
                            -H 'sec-ch-ua: "Not(A:Brand";v="99", "Google Chrome";v="133", "Chromium";v="133"' \\
                            -H 'sec-ch-ua-mobile: ?0' \\
                            -H 'sec-ch-ua-platform: "Windows"' \\
                            -H 'sec-fetch-dest: empty' \\
                            -H 'sec-fetch-mode: cors' \\
                            -H 'sec-fetch-site: same-origin' \\
                            -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36' \\
                            -H 'x-requested-with: XMLHttpRequest'
                """;
        HttpResult httpResult = new ThinEasyCrawl(curl)
                .cookies(cookies)
                .execute();
        JsonHelper jsonHelper = httpResult.getJson();
        System.out.println(jsonHelper.pretty());
    }

    @DisplayName("company info")
    @ParameterizedTest
    @ValueSource(strings = {"SZ000981"})
    public void companyInfo(String symbol) {
        String curl = """
                curl 'https://stock.xueqiu.com/v5/stock/f10/cn/company.json?symbol=${symbol}&md5__1632=n4%2BhGKBImGCAPGNDQrY7KBlIIDCt8bjeD' \\
                  -H 'accept: application/json, text/plain, */*' \\
                  -H 'accept-language: zh-CN,zh;q=0.9' \\
                  -H 'dnt: 1' \\
                  -H 'origin: https://xueqiu.com' \\
                  -H 'priority: u=1, i' \\
                  -H 'referer: https://xueqiu.com/snowman/S/${symbol}/detail' \\
                  -H 'sec-ch-ua: "Google Chrome";v="129", "Not=A?Brand";v="8", "Chromium";v="129"' \\
                  -H 'sec-ch-ua-mobile: ?0' \\
                  -H 'sec-ch-ua-platform: "Windows"' \\
                  -H 'sec-fetch-dest: empty' \\
                  -H 'sec-fetch-mode: cors' \\
                  -H 'sec-fetch-site: same-site' \\
                  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36'
                """;
        HttpResult httpResult = new ThinEasyCrawl(curl)
                .args("symbol", symbol)
                .cookies(cookies)
                .execute();
        JsonHelper jsonHelper = httpResult.getJson().op("data.company").put("stock_code", symbol);
        System.out.println(jsonHelper.pretty());
    }


    @DisplayName("kline")
    @ParameterizedTest
    @ValueSource(strings = {"SZ000981"})
    public void kline(String symbol) {
        String curl = """
                curl 'https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=${symbol}&begin=${begin}&period=day&type=before&count=${count}&indicator=kline,pe,pb,ps,pcf' \\
                  -H 'accept: application/json, text/plain, */*' \\
                  -H 'accept-language: zh-CN,zh;q=0.9' \\
                  -H 'dnt: 1' \\
                  -H 'origin: https://xueqiu.com' \\
                  -H 'priority: u=1, i' \\
                  -H 'referer: https://xueqiu.com/S/${symbol}' \\
                  -H 'sec-ch-ua: "Google Chrome";v="129", "Not=A?Brand";v="8", "Chromium";v="129"' \\
                  -H 'sec-ch-ua-mobile: ?0' \\
                  -H 'sec-ch-ua-platform: "Windows"' \\
                  -H 'sec-fetch-dest: empty' \\
                  -H 'sec-fetch-mode: cors' \\
                  -H 'sec-fetch-site: same-site' \\
                  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36'
                """;
        // 最近30个交易日
        Integer limit = -30;
        //从时间戳后取历史数据
        AtomicLong begin = null;
        HttpResult httpResult = new ThinEasyCrawl(curl)
                .args("count", limit)
                .args("symbol", symbol)
                .args("begin", null == begin ? DateHelper.now().plusDays(1L).timeStamp13() : begin)
                .cookies(cookies)
                .execute();
        JsonHelper jsonHelper = httpResult.getJson().op("data");
        System.out.println(jsonHelper.pretty());
    }

    @DisplayName("Historical Dividends")
    @ParameterizedTest
    @ValueSource(strings = {"SZ000981"})
    public void getBonus(String symbol) {
        String curl =
                """
                        curl 'https://stock.xueqiu.com/v5/stock/f10/cn/bonus.json?symbol=${symbol}&size=60&page=1&extend=true&md5__1632=eqjx0DyDnGGQ0QGC8D%2FQnxSxfOeaz3eb74D' \\
                          -H 'accept: application/json, text/plain, */*' \\
                          -H 'accept-language: zh-CN,zh;q=0.9' \\
                          -H 'cache-control: no-cache' \\
                          -H 'dnt: 1' \\
                          -H 'origin: https://xueqiu.com' \\
                          -H 'pragma: no-cache' \\
                          -H 'priority: u=1, i' \\
                          -H 'referer: https://xueqiu.com/snowman/S/${symbol}/detail' \\
                          -H 'sec-ch-ua: "Google Chrome";v="131", "Chromium";v="131", "Not_A Brand";v="24"' \\
                          -H 'sec-ch-ua-mobile: ?0' \\
                          -H 'sec-ch-ua-platform: "Windows"' \\
                          -H 'sec-fetch-dest: empty' \\
                          -H 'sec-fetch-mode: cors' \\
                          -H 'sec-fetch-site: same-site' \\
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'
                        """;
        JsonHelper data =
                new ThinEasyCrawl(curl).args("symbol", symbol).cookies(cookies).execute().getJson();
        System.out.println(data.pretty());
        String text =
                data.op("data.items")
                        .toIntDate("ashare_ex_dividend_date")
                        .toIntDate("ex_dividend_date")
                        .toIntDate("equity_date")
                        .toIntDate("dividend_date")
                        .pretty();
        System.out.println(text);
    }


}
