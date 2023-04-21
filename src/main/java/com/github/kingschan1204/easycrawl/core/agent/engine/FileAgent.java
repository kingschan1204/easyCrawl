package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.AgentResult;
import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.helper.http.ResponseHeadHelper;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import com.github.kingschan1204.easycrawl.plugs.freemarker.FreemarkParser;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
@Deprecated
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FileAgent extends HttpRequestConfig implements WebAgent<File> {


    @Override
    public WebAgent<File> url(String url) {
        this.setUrl(url);
        return this;
    }

    @Override
    public WebAgent<File> referer(String referer) {
        this.setReferer(referer);
        return this;
    }

    @Override
    public WebAgent<File> method(HttpRequestConfig.Method method) {
        this.setMethod(method);
        return this;
    }

    @Override
    public WebAgent<File> head(Map<String, String> head) {
        this.setHead(head);
        return this;
    }

    @Override
    public WebAgent<File> useAgent(String useAgent) {
        this.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebAgent<File> cookie(Map<String, String> cookie) {
        this.setCookie(cookie);
        return this;
    }

    @Override
    public WebAgent<File> timeOut(Integer timeOut) {
        this.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebAgent<File> proxy(Proxy proxy) {
        this.setProxy(proxy);
        return this;
    }

    @Override
    public WebAgent<File> body(String body) {
        this.setBody(body);
        return this;
    }

    public WebAgent<File> folder(String folder) {
        this.setFolder(folder);
        return this;
    }

    public WebAgent<File> fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    @Override
    public File execute(Map<String, Object> data) throws Exception {
        FreemarkParser parser = new FreemarkParser();
        String httpUrl = parser.parse(this.url, data).trim();
        String tempReferrer = parser.parse(this.referer, data).trim();
        String tempFileName = parser.parse(getFileName(), data).trim();

        log.info("start download file :{}", this.url);
        if (!new File(getFolder()).exists()) {
            FileUtils.forceMkdir(new File(getFolder()));
        }

        AgentResult result = JsoupHelper.request(httpUrl, this.method(), this.timeOut, this.useAgent, this.referer, this.head, this.cookie, this.proxy, true, true, this.body);
        String defaultFileName = null;
        if (result.getStatusCode() != 200) {
            log.error("文件下载失败：{}", this.url);
            throw new Exception(String.format("文件下载失败：%s 返回码:%s", this.url, result.getStatusCode()));
        }
        if (result.getContentType().contains("name")) {
            String[] list = result.getContentType().split(";");
            defaultFileName = Arrays.stream(list)
                    .filter(s -> s.startsWith("name")).findFirst().get().replaceAll("name=|\"", "");
        }
        if (result.getHeaders().containsKey(ResponseHeadHelper.CONTENT_DISPOSITION)) {
            //attachment;filename=%E8%A1%8C%E4%B8%9A%E5%88%86%E7%B1%BB.xlsx
            String s = result.getHeaders().get(ResponseHeadHelper.CONTENT_DISPOSITION);
            String decode = URLDecoder.decode(s, "UTF-8");
            defaultFileName = decode.replaceAll(".*=", "");
        }
        //文件名优先使用指定的文件名，如果没有指定 则获取自动识别的文件名
        this.setFileName(String.valueOf(getFileName()).matches(RegexHelper.REGEX_FILE_NAME) ? getFileName() : defaultFileName);
        Assert.notNull(getFileName(), "文件名不能为空！");
        String path = String.format("%s%s", getFolder(), getFileName());
        // output here
        log.info("输出文件：{}", path);
        FileOutputStream out = null;
        File file = new File(path);
        try {
            out = (new FileOutputStream(file));
            out.write(result.getBodyAsByes());
        } catch (Exception ex) {
            log.error("文件下载失败：{} {}", this.url,ex);
            ex.printStackTrace();
        } finally {
            assert out != null;
            out.close();
        }
        return file;
    }

    


}
