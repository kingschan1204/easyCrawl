package com.github.kingschan1204.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.collections.MapUtil;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import com.github.kingschan1204.excel.dto.Gdrs;
import com.github.kingschan1204.excel.dto.Quote;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

@Slf4j
@DisplayName("指定模板-导出excel测试")
public class ExportExcelTest {



    Map<String, String> getXQCookies(){
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }



    @DisplayName("导出excel测试")
    @Test
    public void exportExcel() {
        String code ="SZ002304";
        Map<String, String> cookies = getXQCookies();
        Map<String, Object> args = new MapUtil<String,Object>().put("code",code).getMap();

        //个股详情
        String apiUrl = "https://stock.xueqiu.com/v5/stock/quote.json?symbol=${code}&extend=detail";
        String referer = "https://xueqiu.com/S/${code}";
        String result = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().referer(referer).cookie(cookies).url(apiUrl))
                .analyze(r -> r.getResult().getBody()).execute(args);
        JsonHelper quoteJsonHelper = JsonHelper.of(result);
        com.alibaba.fastjson2.JSONObject quoteJson = quoteJsonHelper.get("data.quote",com.alibaba.fastjson2.JSONObject.class);
        Quote quote = quoteJson.toJavaObject(Quote.class);
        System.out.println(quote);

        //股东人数
        String page = "https://xueqiu.com/snowman/S/${code}/detail#/GDRS";
        apiUrl = "https://stock.xueqiu.com/v5/stock/f10/cn/holders.json?symbol=${code}&extend=true&page=1&size=100";
        String data = new EasyCrawl<String>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(page).cookie(cookies))
                .analyze(WebAgent::getText)
                .execute(args);
        JsonHelper gdrsJsonHelper = JsonHelper.of(data);
        com.alibaba.fastjson2.JSONArray gdrsJson = gdrsJsonHelper.get("data.items",com.alibaba.fastjson2.JSONArray.class);
        List<Gdrs> gdrs = gdrsJson.toJavaList(Gdrs.class);
        System.out.println(gdrs);

        // 模板文件路径
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // 获取资源文件的URL
        java.net.URL resourceUrl = classLoader.getResource("stock.xlsx");
        String templateFileName = resourceUrl.getPath();
        // 输出文件路径
        String outputFileName = "./stock_output.xlsx";

        // 填充数据到模板
        ExcelWriter excelWriter = EasyExcel.write(outputFileName).withTemplate(templateFileName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        //开启自动换行,自动换行表示每次写入一条list数据是都会重新生成一行空行,此选项默认是关闭的,需要提前设置为true
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(gdrs,fillConfig, writeSheet);
        excelWriter.fill(quote, fillConfig, writeSheet);


        excelWriter.finish();

        ;
    }
}
