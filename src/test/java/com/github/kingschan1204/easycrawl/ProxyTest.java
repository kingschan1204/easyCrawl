package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
@DisplayName("代理测试")
public class ProxyTest {

    String useAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    String apiUrl = "https://myip.ipip.net/";
    String ip = "117.74.65.29";
    int port = 7890;
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));

    @DisplayName("代理ip测试")
    @Test
    public void proxyTest() {
        String result = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().referer(apiUrl).useAgent(useAgent).url(apiUrl).proxy(proxy))
                .analyze(r -> r.getResult().getBody()).execute();
        System.out.println(result);
    }
}
