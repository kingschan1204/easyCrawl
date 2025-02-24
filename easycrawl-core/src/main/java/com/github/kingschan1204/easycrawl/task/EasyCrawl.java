package com.github.kingschan1204.easycrawl.task;

import com.github.kingschan1204.easycrawl.core.agent.WebAgent;
import com.github.kingschan1204.easycrawl.core.agent.dto.HttpRequestConfig;
import com.github.kingschan1204.easycrawl.core.agent.dto.ProxyConfig;
import com.github.kingschan1204.easycrawl.core.enums.HttpRequestEngine;
import com.github.kingschan1204.easycrawl.core.util.http.CURLHelper;
import com.github.kingschan1204.easycrawl.core.util.http.UrlHelper;
import com.github.kingschan1204.easycrawl.helper.json.JsonHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * 2023-4-11
 *
 * @author kingschan
 */
@Slf4j
public class EasyCrawl<R> {

  protected WebAgent webAgent;
  protected Function<WebAgent, R> parserFunction;
  protected Map<String, Object> argsMap;

  public EasyCrawl<R> webAgent(WebAgent webAgent) {
    this.webAgent = webAgent;
    return this;
  }

  public EasyCrawl<R> webAgent(String curl) {
    return webAgent(curl, null);
  }

  public EasyCrawl<R> proxy(ProxyConfig config) {
    this.webAgent = this.webAgent.proxy(config);
    return this;
  }

  public EasyCrawl<R> webAgent(String curl, HttpRequestEngine engine) {
    HttpRequestConfig config = new CURLHelper(curl).getConfig();
    this.webAgent = WebAgent.agent(config, engine);
    return this;
  }

  public EasyCrawl<R> analyze(Function<WebAgent, R> parserFunction) {
    this.parserFunction = parserFunction;
    return this;
  }

  public EasyCrawl<R> timeOut(Integer timeOut) {
    this.webAgent.timeOut(timeOut);
    return this;
  }

  public EasyCrawl<R> args(String key, Object value) {
    if (null == argsMap) {
      argsMap = new HashMap<>();
    }
    argsMap.put(key, value);
    return this;
  }

  public EasyCrawl<R> args(Map<String, Object> map) {
    if (null == argsMap) {
      argsMap = map;
    }
    this.argsMap.putAll(map);
    return this;
  }

  public EasyCrawl<R> cookies(Map<String, String> map) {
    this.webAgent.cookie(map);
    return this;
  }

  public EasyCrawl<R> heads(Map<String, String> heads) {
    this.webAgent.head(heads);
    return this;
  }

  public EasyCrawl<R> head(String key, String value) {
    this.webAgent.head(key, value);
    return this;
  }

  public EasyCrawl<R> method(HttpRequestConfig.Method method) {
    this.webAgent.method(method);
    return this;
  }

  public EasyCrawl<R> body(String body) {
    this.webAgent.body(body);
    return this;
  }

  public EasyCrawl<R> folder(String path) {
    this.webAgent.folder(path);
    return this;
  }

  public R execute() {
    Assert.notNull(webAgent, "agent对象不能为空！");
    Assert.notNull(parserFunction, "解析函数不能为空！");
    R result = null;
    CompletableFuture<R> cf =
        CompletableFuture.supplyAsync(
                () -> {
                  try {
                    return webAgent.execute(this.argsMap);
                  } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                  }
                })
            .thenApply(parserFunction);
    try {
      result = cf.get();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
    return result;
  }

  /**
   * restApi json格式自动获取所有分页
   *
   * @param map 运行参数
   * @param pageIndexKey 页码key
   * @param totalKey 总记录条数key
   * @param pageSize
   * @return
   */
  public List<R> executePage(
      Map<String, Object> map, String pageIndexKey, String totalKey, Integer pageSize) {
    List<R> list = Collections.synchronizedList(new ArrayList<>());
    WebAgent data = webAgent.execute(map);
    JsonHelper json = data.getResult().getJson();
    Integer totalRows = json.get(totalKey).intValue();
    Integer totalPage = (totalRows + pageSize - 1) / pageSize;
    log.debug("共{}记录,每页展示{}条,共{}页", totalRows, pageSize, totalPage);

    List<CompletableFuture<R>> cfList = new ArrayList<>();
    Consumer<R> consumer =
        (r) -> {
          if (r instanceof Collection) {
            list.addAll((Collection<? extends R>) r);
          } else {
            list.add(r);
          }
        };
    cfList.add(CompletableFuture.supplyAsync(() -> data).thenApply(parserFunction));
    cfList.get(0).thenAccept(consumer);

    for (int i = 2; i <= totalPage; i++) {
      String url =
          new UrlHelper(webAgent.getConfig().getUrl())
              .set(pageIndexKey, String.valueOf(i))
              .getUrl();
      CompletableFuture<R> cf =
          CompletableFuture.supplyAsync(
                  () -> {
                    try {
                      return webAgent.url(url).execute(map);
                    } catch (Exception e) {
                      e.printStackTrace();
                      return null;
                    }
                  })
              .thenApply(parserFunction);
      cf.thenAccept(consumer);
      cfList.add(cf);
    }
    CompletableFuture.allOf(cfList.toArray(new CompletableFuture[] {})).join();
    return list;
  }
}
