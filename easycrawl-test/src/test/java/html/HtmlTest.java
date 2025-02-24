package html;

import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("cookie相关测试")
public class HtmlTest {

  @DisplayName("公司资料")
  @Test
  public void company() throws IOException {
    String curl =
        """
                curl 'https://basic.10jqka.com.cn/600519/company.html' \\
                  -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7' \\
                  -H 'Accept-Language: zh-CN,zh;q=0.9' \\
                  -H 'Cache-Control: no-cache' \\
                  -H 'Connection: keep-alive' \\
                  -H 'DNT: 1' \\
                  -H 'Pragma: no-cache' \\
                  -H 'Sec-Fetch-Dest: document' \\
                  -H 'Sec-Fetch-Mode: navigate' \\
                  -H 'Sec-Fetch-Site: none' \\
                  -H 'Sec-Fetch-User: ?1' \\
                  -H 'Upgrade-Insecure-Requests: 1' \\
                  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36' \\
                  -H 'sec-ch-ua: "Google Chrome";v="131", "Chromium";v="131", "Not_A Brand";v="24"' \\
                  -H 'sec-ch-ua-mobile: ?0' \\
                  -H 'sec-ch-ua-platform: "Windows"'
                """;
    Map<String, String> map =
        new EasyCrawl<Map<String, String>>()
            .webAgent(curl)
            .analyze(
                r -> {
                  Map<String, String> m = new HashMap<>();
                  String content = r.getResult().getText();
                  Document doc = Jsoup.parse(content);
                  Elements elements =
                      doc.select(
                          "#detail > div.bd > table > tbody > tr.video-btn-box-tr > td:nth-child(2) > span");
                  m.put("name", elements.text());
                  m.put(
                      "control",
                      doc.select(
                              "#detail > div.bd > div > table > tbody > tr:nth-child(4) > td > div > span")
                          .text());
                  m.put(
                      "url",
                      doc.select(
                              "#detail > div.bd > table > tbody > tr:nth-child(3) > td:nth-child(2) > span")
                          .text());
                  return m;
                })
            .execute();
    System.out.println(map);
  }
}
