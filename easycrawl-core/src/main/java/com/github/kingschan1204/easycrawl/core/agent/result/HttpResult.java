package com.github.kingschan1204.easycrawl.core.agent.result;

import com.github.kingschan1204.easycrawl.core.agent.dto.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import java.io.File;
import java.util.Map;

public interface HttpResult {
  HttpRequestConfig getConfig();

  Long timeMillis();

  Integer statusCode();

  String charset();

  String contentType();

  Map<String, String> cookies();

  byte[] bodyAsByes();

  String body();

  void setBody(String body);

  Map<String, String> headers();

  JsonHelper getJson();

  String getText();

  File getFile();
}
