package com.github.kingschan1204.easycrawl.core.agent;

import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.core.variable.ScanVariable;
import com.github.kingschan1204.easycrawl.helper.http.ResponseHeadHelper;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.util.Map;

/**
 * @author kingschan
 * 2023-4-24
 */
@Slf4j
public class GenericHttp1Agent implements WebAgent {

    private HttpRequestConfig config;
    private AgentResult result;

    public GenericHttp1Agent(HttpRequestConfig config) {
        this.config = config;
    }

    public GenericHttp1Agent() {
        this.config = new HttpRequestConfig();
    }

    public static WebAgent of(HttpRequestConfig config) {
        return new GenericHttp1Agent(config);
    }

//
//    @Override
//    public WebAgentNew of(HttpRequestConfig config) {
//        this.config = config;
//        return this;
//    }

    @Override
    public HttpRequestConfig getConfig() {
        return this.config;
    }

    @Override
    public WebAgent url(String url) {
        this.config.setUrl(url);
        return this;
    }

    @Override
    public WebAgent referer(String referer) {
        this.config.setReferer(referer);
        return this;
    }

    @Override
    public WebAgent method(HttpRequestConfig.Method method) {
        this.config.setMethod(method);
        return this;
    }

    @Override
    public WebAgent head(Map<String, String> head) {
        this.config.setHead(head);
        return this;
    }

    @Override
    public WebAgent useAgent(String useAgent) {
        this.config.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebAgent cookie(Map<String, String> cookie) {
        this.config.setCookie(cookie);
        return this;
    }

    @Override
    public WebAgent timeOut(Integer timeOut) {
        this.config.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebAgent proxy(Proxy proxy) {
        this.config.setProxy(proxy);
        return this;
    }

    @Override
    public WebAgent body(String body) {
        this.config.setBody(body);
        return this;
    }

    @Override
    public WebAgent folder(String folder) {
        this.config.setFolder(folder);
        return this;
    }

    @Override
    public WebAgent fileName(String fileName) {
        this.config.setFileName(fileName);
        return this;
    }

    @Override
    public WebAgent execute(Map<String, Object> data) {
        String httpUrl = ScanVariable.parser(this.config.getUrl(), data).trim();
        String referer = null != this.config.getReferer() ? ScanVariable.parser(this.config.getReferer(), data).trim() : null;
        this.result = JsoupHelper.request(
                httpUrl, this.config.method(),
                this.config.getTimeOut(), this.config.getUseAgent(), referer, this.config.getHead(),
                this.config.getCookie(), this.config.getProxy(),
                true, true, this.config.getBody());
        return this;
    }

    @Override
    public AgentResult getResult() {
        return this.result;
    }

    @Override
    public JsonHelper getJson() {
        return JsonHelper.of(this.result.getBody());
    }

    @Override
    public String getText() {
        return getResult().getBody();
    }

    @Override
    public File getFile() {
        Assert.notNull(this.result, "返回对象为空！或者程序还未执行execute方法！");
        ResponseHeadHelper headHelper = ResponseHeadHelper.of(result.getHeaders());
        Assert.isTrue(headHelper.fileContent(), "非文件流请求，无法输出文件！");
        String defaultFileName = null;
        File file = null;
        if (result.getStatusCode() != 200) {
            log.error("文件下载失败：{}", this.config.getUrl());
            throw new RuntimeException(String.format("文件下载失败：%s 返回码:%s", this.config.getUrl(), result.getStatusCode()));
        }
        try {
            defaultFileName = headHelper.getFileName();
            //文件名优先使用指定的文件名，如果没有指定 则获取自动识别的文件名
            this.config.setFileName(String.valueOf(this.config.getFileName()).matches(RegexHelper.REGEX_FILE_NAME) ? this.config.getFileName() : defaultFileName);
            Assert.notNull(this.config.getFileName(), "文件名不能为空！");
            String path = String.format("%s%s", this.config.getFolder(), this.config.getFileName());
            // output here
            log.info("输出文件：{}", path);
            FileOutputStream out = null;
            file = new File(path);
            try {
                out = (new FileOutputStream(file));
                out.write(result.getBodyAsByes());
            } catch (Exception ex) {
                log.error("文件下载失败：{} {}", this.config.getUrl(), ex);
                ex.printStackTrace();
            } finally {
                assert out != null;
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return file;
    }
}
