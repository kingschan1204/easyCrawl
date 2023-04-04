package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebDataAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.FileOperationUtils;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.plugs.freemarker.FreemarkParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.Proxy;
import java.util.Map;

@Data
@Slf4j
public final class FileEngine implements WebDataAgent<File> {

    private String folder;
    private String fileName;
    HttpEngine engine = new HttpEngine();

    @Override
    public WebDataAgent<File> url(String url) {
        this.engine.setUrl(url);
        return this;
    }

    @Override
    public WebDataAgent<File> referer(String referer) {
        this.engine.setReferer(referer);
        return this;
    }

    @Override
    public WebDataAgent<File> method(HttpEngine.Method method) {
        this.engine.setMethod(method);
        return this;
    }

    @Override
    public WebDataAgent<File> head(Map<String, String> head) {
        this.engine.setHead(head);
        return this;
    }

    @Override
    public WebDataAgent<File> useAgent(String useAgent) {
        this.engine.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebDataAgent<File> cookie(Map<String, String> cookie) {
        this.engine.setCookie(cookie);
        return this;
    }

    @Override
    public WebDataAgent<File> timeOut(Integer timeOut) {
        this.engine.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebDataAgent<File> proxy(Proxy proxy) {
        this.engine.setProxy(proxy);
        return this;
    }

    @Override
    public File dataPull(Map<String, Object> data) throws Exception {
        FreemarkParser parser = new FreemarkParser();
        String httpUrl = parser.parse(this.engine.url, data).trim();
        String tempReferrer = parser.parse(this.engine.referer, data).trim();
        String tempFileName = parser.parse(fileName, data).trim();
        return download(httpUrl, tempReferrer, this.folder, tempFileName);
    }

    @Override
    public HttpEngine get() {
        return this.engine;
    }

    File download(String url, String referrer, String folder, String fileName) throws Exception {
        String localPath = String.format("%s%s", folder, fileName);
        if (String.valueOf(fileName).matches(RegexHelper.REGEX_FILE_NAME) && new File(localPath).exists()) {
            log.info("{}文件存在直接读取：", localPath);
            return new File(localPath);
        }
        return FileOperationUtils.downloadFile(url, referrer, folder, fileName, this.engine.useAgent);
    }
}
