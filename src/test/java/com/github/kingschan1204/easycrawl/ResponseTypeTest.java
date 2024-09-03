package com.github.kingschan1204.easycrawl;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.helper.collections.MapUtil;
import com.github.kingschan1204.easycrawl.task.EasyCrawl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

@DisplayName("各文件类型测试")
public class ResponseTypeTest {

    @Test
    public void test() {
        Map<String, String> map = new MapUtil<String, String>()
                .put("js", "https://cdn.bootcdn.net/ajax/libs/jquery/3.6.4/jquery.min.js")
                .put("css", "https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.2.3/css/bootstrap-grid.min.css")
                .put("html", "https://www.bootcdn.cn/")
                .put("png", "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
                .put("pdf", "http://notice.10jqka.com.cn/api/pdf/6cb73f7b9c9925c3.pdf")
                .put("text", "https://myip.ipip.net/")
                .put("json", "https://open.xueqiu.com/mpaas/config/content?appkey=37c480a66a8aa8450910b5464ce96e26&keys=taichi_run_flag&uid=9920891343")
                .getMap();
        map.forEach((k,v)->{
            String result = new EasyCrawl<String>().webAgent(WebAgent.defaultAgent().url(v).timeOut(8000)).analyze(WebAgent::getText).execute();
            if(k.matches("text")){
                System.out.println(result);
            }
        });

    }
}
