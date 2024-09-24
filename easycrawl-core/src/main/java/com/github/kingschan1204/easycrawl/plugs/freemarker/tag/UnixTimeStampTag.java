package com.github.kingschan1204.easycrawl.plugs.freemarker.tag;

import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import freemarker.core.Environment;
import freemarker.template.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class UnixTimeStampTag implements TemplateDirectiveModel {

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_32);
        log.debug("Freemarker 自定义标签传入参数：{}", JsonHelper.of(map));
        //默认输出13位
        String type = map.containsKey("len") ? String.valueOf(map.get("len")) : "13";
        Assert.isTrue(type.matches("10|13"), "时间戳只支持10位或者13位！");
        long timeStamp = type.equals("13") ? System.currentTimeMillis() : System.currentTimeMillis() / 1000;
        TemplateModel tm = builder.build().wrap(timeStamp);
        environment.setVariable("v", tm);
        templateDirectiveBody.render(environment.getOut());
    }
}
