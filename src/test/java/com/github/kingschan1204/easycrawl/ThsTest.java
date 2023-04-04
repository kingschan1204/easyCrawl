package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.engine.FileEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
@DisplayName("同花顺测试")
public class ThsTest {

    String url = "http://basic.10jqka.com.cn/api/stock/export.php?export=main&type=year&code=600519";
    String referer = "http://basic.10jqka.com.cn/api/stock/export.php?export=main&type=year&code=600519";
    String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3346.9 Safari/537.36";

    @DisplayName("年报下载")
    @Test
    public void getYearReport() throws Exception {
        FileEngine engine = new FileEngine();
        engine.referer(referer);
        engine.useAgent(agent);
        engine.url(url);
        engine.method(HttpEngine.Method.GET);
        engine.setFolder("C:\\temp\\");
//        engine.setFileName("600519.xls");
        File file = engine.dataPull(null);
        System.out.println(file.getName());

    }
}
