package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.GenericHttp1Agent;
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

        File file= new GenericHttp1Agent()
                .folder("C:\\temp\\")
                .referer(referer)
                .url(url)
                .execute(null)
                .getFile();
        System.out.println(file.getName());

    }
}
