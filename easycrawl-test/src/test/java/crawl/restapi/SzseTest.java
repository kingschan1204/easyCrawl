package crawl.restapi;

import com.github.kingschan1204.easycrawl.core.agent.result.HttpResult;
import com.github.kingschan1204.easycrawl.task.ThinEasyCrawl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SzseTest")
public class SzseTest {
    String curl = """
            curl 'https://www.szse.cn/api/report/exchange/onepersistenthour/monthList?month=${month}&random=0.14689129800287826' \\
              -H 'Accept: application/json, text/javascript, */*; q=0.01' \\
              -H 'Accept-Language: zh-CN,zh;q=0.9' \\
              -H 'Connection: keep-alive' \\
              -H 'Content-Type: application/json' \\
              -H 'DNT: 1' \\
              -H 'Referer: https://www.szse.cn/disclosure/index.html' \\
              -H 'Sec-Fetch-Dest: empty' \\
              -H 'Sec-Fetch-Mode: cors' \\
              -H 'Sec-Fetch-Site: same-origin' \\
              -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36' \\
              -H 'X-Request-Type: ajax' \\
              -H 'X-Requested-With: XMLHttpRequest' \\
              -H 'sec-ch-ua: "Not(A:Brand";v="99", "Google Chrome";v="133", "Chromium";v="133"' \\
              -H 'sec-ch-ua-mobile: ?0' \\
              -H 'sec-ch-ua-platform: "Windows"'
            """;

    @ParameterizedTest
    @ValueSource(strings = {"","2024-01"})
    @DisplayName("tradingCalendar")
    public void tradingCalendar(String month){
        HttpResult httpResult = new ThinEasyCrawl(curl).args("month",month).execute();
        System.out.println(httpResult.getJson().pretty());
        // zrxh: 自然序号 星期1~星期天 星期天是 1 星期一是 2
        // jybz: 交易标志 1交易日  0 非交易日
        // jyrq: 交易日期 YYYY-MM-DD
        // nowdate : 当前日期 YYYY-MM-DD
    }
}
