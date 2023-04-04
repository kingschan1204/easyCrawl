package com.github.kingschan1204.easycrawl.core.agent.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author kings.chan
 * @create 2019-03-07 9:33
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebPage implements Serializable {
    //请求耗时  毫秒
    private Long timeMillis;
    private Integer statusCode;
    private String statusMessage;
    private String charset;
    private String contentType;
    private String body;
    //返回的cookies
    private Map<String,String> cookies;


}
