package com.github.kingschan1204.easycrawl.core.agent.utils;

import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * 文件操作
 *
 * @author kings.chan
 * @create 2020-02-17 17:34
 **/
@Slf4j
public class FileOperationUtils {
    /**
     * 通过指定的文件下载URL以及下载目录下载文件
     *
     * @param url      下载url路径
     * @param referrer 来源
     * @param dir      存放目录
     * @param filename 文件名
     * @throws Exception
     */
    public static File downloadFile(String url, String referrer, String dir, String filename, String agent) throws Exception {
        log.info("start download file :{}", url);
        if (!new File(dir).exists()) {
            FileUtils.forceMkdir(new File(dir));
        }
        //Open a URL Stream
        Connection.Response resultResponse = Jsoup.connect(url)
                .userAgent(agent)
                .referrer(referrer)
                .ignoreContentType(true).execute();
        String defaultFileName = null;
        if (resultResponse.statusCode() != 200) {
            log.error("文件下载失败：{}", url);
            throw new Exception(String.format("文件下载失败：%s 返回码:%s", url, resultResponse.statusCode()));
        }
        if (resultResponse.contentType().contains("name")) {
            String[] list = resultResponse.contentType().split(";");
            defaultFileName = Arrays.stream(list)
                    .filter(s -> s.startsWith("name")).findFirst().get().replaceAll("name=|\"", "");
        }
        // output here
        String path = dir + (filename.matches(RegexHelper.REGEX_FILE_NAME) ?  filename : defaultFileName);
        log.info("输出文件：{}",path);
        FileOutputStream out = null;
        File file = new File(path);
        try {
            out = (new FileOutputStream(file));
            out.write(resultResponse.bodyAsBytes());
        } catch (Exception ex) {
            log.error("{}", ex);
            log.error("文件下载失败：{}", url);
            ex.printStackTrace();
        } finally {
            assert out != null;
            out.close();
        }
        return file;
    }



}
