package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebDataAgent;
import com.github.kingschan1204.easycrawl.core.agent.utils.JsoupHelper;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.plugs.freemarker.FreemarkParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLDecoder;
import java.util.Arrays;
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
    public WebDataAgent<File> body(String body) {
        this.engine.setBody(body);
        return this;
    }

    @Override
    public File dataPull(Map<String, Object> data) throws Exception {
        FreemarkParser parser = new FreemarkParser();
        String httpUrl = parser.parse(this.engine.url, data).trim();
        String tempReferrer = parser.parse(this.engine.referer, data).trim();
        String tempFileName = parser.parse(fileName, data).trim();


        Connection.Method m;
        switch (this.engine.method) {
            case GET:
                m = Connection.Method.GET;
                break;
            case POST:
                m = Connection.Method.POST;
                break;
            default:
                throw new RuntimeException("目前只支持：get , post 方法！");
        }

        String localPath = String.format("%s%s", folder, fileName);
       /* if (String.valueOf(fileName).matches(RegexHelper.REGEX_FILE_NAME) && new File(localPath).exists()) {
            log.info("{}文件存在直接读取：", localPath);
            return new File(localPath);
        }*/

        log.info("start download file :{}", this.engine.url);
        if (!new File(this.folder).exists()) {
            FileUtils.forceMkdir(new File(this.folder));
        }
        //Open a URL Stream
        Connection.Response resultResponse = JsoupHelper.buildConnection(httpUrl, m,
                this.engine.timeOut, this.engine.useAgent, this.engine.referer, this.engine.head,
                this.engine.cookie, this.engine.proxy,
                true, true, this.engine.body).execute();
//                Jsoup.connect(this.engine.url)
//                .userAgent(this.engine.useAgent)
//                .referrer(this.engine.referer)
//                .cookies(this.engine.cookie)
//                .method(m)
//                .requestBody(this.engine.body)
//                .ignoreContentType(true).execute();
        String defaultFileName = null;
        if (resultResponse.statusCode() != 200) {
            log.error("文件下载失败：{}", this.engine.url);
            throw new Exception(String.format("文件下载失败：%s 返回码:%s", this.engine.url, resultResponse.statusCode()));
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
            fileName = decode.replaceAll(".*=", "");

        }
        fileName = null == fileName ? "xxx.xls" : fileName;
        // output here
        String path = this.folder + (fileName.matches(RegexHelper.REGEX_FILE_NAME) ? fileName : defaultFileName);
        log.info("输出文件：{}", path);
        FileOutputStream out = null;
        File file = new File(path);
        try {
            out = (new FileOutputStream(file));
            out.write(resultResponse.bodyAsBytes());
        } catch (Exception ex) {
            log.error("{}", ex);
            log.error("文件下载失败：{}", this.engine.url);
            ex.printStackTrace();
        } finally {
            assert out != null;
            out.close();
        }
        return file;
    }

    @Override
    public HttpEngine get() {
        return this.engine;
    }


}
