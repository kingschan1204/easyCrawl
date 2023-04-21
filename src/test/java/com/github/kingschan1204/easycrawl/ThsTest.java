package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.WebAgentNew;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
@DisplayName("同花顺测试")
public class ThsTest {

    String url = "http://basic.10jqka.com.cn/api/stock/export.php?export=main&type=year&code=600519";
    String referer = "http://basic.10jqka.com.cn/api/stock/export.php?export=main&type=year&code=600519";

    @DisplayName("年报下载")
    @Test
    public void getYearReport() throws Exception {
        File file = new EasyCrawl<File>()
                .webAgent(WebAgentNew.defaultAgent().folder("C:\\temp\\")
                        .referer(referer)
                        .url(url))
                .analyze(WebAgentNew::getFile)
                .execute();
        System.out.println(String.format("文件上名：%s 文件大小：%s kb", file.getName(), file.length() / 1024));
    }
}
