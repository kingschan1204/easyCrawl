package com.github.kingschan1204.easycrawl.core.agent.utils;

import com.github.kingschan1204.easycrawl.app.Application;
import com.github.kingschan1204.easycrawl.core.agent.dto.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.core.agent.result.HttpResult;
import com.github.kingschan1204.easycrawl.core.util.http.ResponseHeadHelper;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpFileHelper {
  public static File downloadFile(HttpResult result, HttpRequestConfig config) {
    Assert.notNull(result, "返回对象为空！或者程序还未执行execute方法！");
    ResponseHeadHelper headHelper = ResponseHeadHelper.of(result.headers());
    Assert.isTrue(headHelper.fileContent(), "非文件流请求，无法输出文件！");
    String defaultFileName = null;
    File file = null;
    if (result.statusCode() != 200) {
      log.error("文件下载失败：{}", config.getUrl());
      throw new RuntimeException(
          String.format("文件下载失败：%s 返回码:%s", config.getUrl(), result.statusCode()));
    }
    try {
      defaultFileName = headHelper.getFileName();
      // 文件名优先使用指定的文件名，如果没有指定 则获取自动识别的文件名
      config.setFileName(
          String.valueOf(config.getFileName()).matches(RegexHelper.REGEX_FILE_NAME)
              ? config.getFileName()
              : defaultFileName);
      Assert.notNull(config.getFileName(), "文件名不能为空！");
      String folder =
          Optional.ofNullable(config.getFolder())
              .orElse(Application.getInstance().getDefaultConfig().getFileFolder());
      String path = String.format("%s%s", folder, config.getFileName());
      // output here
      log.info("输出文件：{}", path);
      FileOutputStream out = null;
      file = new File(path);
      try {
        out = (new FileOutputStream(file));
        out.write(result.bodyAsByes());
      } catch (Exception ex) {
        log.error("文件下载失败：{} {}", config.getUrl(), ex);
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
