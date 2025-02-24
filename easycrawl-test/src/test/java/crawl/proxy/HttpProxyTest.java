package crawl.proxy;

import com.github.kingschan1204.easycrawl.core.agent.dto.ProxyConfig;
import com.github.kingschan1204.easycrawl.core.agent.result.HttpResult;
import com.github.kingschan1204.easycrawl.core.enums.HttpRequestEngine;
import com.github.kingschan1204.easycrawl.task.ThinEasyCrawl;
import java.net.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("http代理测试")
public class HttpProxyTest {
  String apiUrl = "https://myip.ipip.net/json";

  @DisplayName("http代理测试")
  @Test
  public void httpTest() {
    ProxyConfig proxy = ProxyConfig.of(Proxy.Type.HTTP, "127.0.0.1", 27890);
    HttpResult result = new ThinEasyCrawl(apiUrl).proxy(proxy).execute();
    System.out.println(result.body());
  }

  @DisplayName("socket5代理测试")
  @Test
  public void socket5Test() {
    // 暂时只有Jsoup支持socket5代理
    ProxyConfig proxy = ProxyConfig.of(Proxy.Type.SOCKS, "127.0.0.1", 37890);
    HttpResult result = new ThinEasyCrawl(apiUrl, HttpRequestEngine.JSOUP).proxy(proxy).execute();
    System.out.println(result.body());
  }
}
