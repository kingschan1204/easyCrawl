package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebDataAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import com.github.kingschan1204.easycrawl.plugs.freemarker.FreemarkParser;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Slf4j
public final class FileEngine extends HttpEngine implements WebDataAgent<File> {

    //下载文件上的时候才有作用
    private String folder;
    private String fileName;

    public WebDataAgent<File> folder(String folder) {
        this.folder = folder;
        return this;
    }

    public WebDataAgent<File> fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public WebDataAgent<File> url(String url) {
        this.setUrl(url);
        return this;
    }

    @Override
    public WebDataAgent<File> referer(String referer) {
        this.setReferer(referer);
        return this;
    }

    @Override
    public WebDataAgent<File> method(HttpEngine.Method method) {
        this.setMethod(method);
        return this;
    }

    @Override
    public WebDataAgent<File> head(Map<String, String> head) {
        this.setHead(head);
        return this;
    }

    @Override
    public WebDataAgent<File> useAgent(String useAgent) {
        this.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebDataAgent<File> cookie(Map<String, String> cookie) {
        this.setCookie(cookie);
        return this;
    }

    @Override
    public WebDataAgent<File> timeOut(Integer timeOut) {
        this.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebDataAgent<File> proxy(Proxy proxy) {
        this.setProxy(proxy);
        return this;
    }

    @Override
    public WebDataAgent<File> body(String body) {
        this.setBody(body);
        return this;
    }

    @Override
    public File dataPull(Map<String, Object> data) throws Exception {
        FreemarkParser parser = new FreemarkParser();
        String httpUrl = parser.parse(this.url, data).trim();
        String tempReferrer = parser.parse(this.referer, data).trim();
        String tempFileName = parser.parse(fileName, data).trim();


        Connection.Method m;
        switch (this.method) {
            case GET:
                m = Connection.Method.GET;
                break;
            case POST:
                m = Connection.Method.POST;
                break;
            default:
                throw new RuntimeException("目前只支持：get , post 方法！");
        }
//        String localPath = String.format("%s%s", folder, tempFileName);
       /* if (String.valueOf(fileName).matches(RegexHelper.REGEX_FILE_NAME) && new File(localPath).exists()) {
            log.info("{}文件存在直接读取：", localPath);
            return new File(localPath);
        }*/

        log.info("start download file :{}", this.url);
        if (!new File(this.folder).exists()) {
            FileUtils.forceMkdir(new File(this.folder));
        }
        //Open a URL Stream
        Connection.Response resultResponse = JsoupHelper.buildConnection(httpUrl, m,
                this.timeOut, this.useAgent, this.referer, this.head,
                this.cookie, this.proxy,
                true, true, this.body).execute();
        String defaultFileName = null;
        if (resultResponse.statusCode() != 200) {
            log.error("文件下载失败：{}", this.url);
            throw new Exception(String.format("文件下载失败：%s 返回码:%s", this.url, resultResponse.statusCode()));
        }
        if (resultResponse.contentType().contains("name")) {
            String[] list = resultResponse.contentType().split(";");
            defaultFileName = Arrays.stream(list)
                    .filter(s -> s.startsWith("name")).findFirst().get().replaceAll("name=|\"", "");
        }
        if (resultResponse.hasHeader("Content-disposition")) {
            //attachment;filename=%E8%A1%8C%E4%B8%9A%E5%88%86%E7%B1%BB.xlsx
            String s = resultResponse.header("Content-disposition");
            String decode = URLDecoder.decode(s, "UTF-8");
            defaultFileName = decode.replaceAll(".*=", "");
        }
        //文件名优先使用指定的文件名，如果没有指定 则获取自动识别的文件名
        fileName = String.valueOf(fileName).matches(RegexHelper.REGEX_FILE_NAME) ? fileName : defaultFileName;
        Assert.notNull(fileName, "文件名不能为空！");
        String path = String.format("%s%s", folder, fileName);
        // output here
        log.info("输出文件：{}", path);
        FileOutputStream out = null;
        File file = new File(path);
        try {
            out = (new FileOutputStream(file));
            out.write(resultResponse.bodyAsBytes());
        } catch (Exception ex) {
            log.error("{}", ex);
            log.error("文件下载失败：{}", this.url);
            ex.printStackTrace();
        } finally {
            assert out != null;
            out.close();
        }
        return file;
    }

    @Override
    public HttpEngine get() {
        return this;
    }


}
