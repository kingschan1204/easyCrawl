package com.github.kingschan1204.easycrawl.schedule.queue;

import java.util.Map;

/**
 * @author kingschan
 */
public interface RedisQueue {
  String redisKey();

  /**
   * 从队列中取出一个元素并删除
   *
   * @return
   */
  Map<String, Object> leftPop();

  /**
   * 往头部添加一个元素
   *
   * @param value
   */
  void leftPush(Map<String, Object> value);

  /**
   * 队列长度
   *
   * @return
   */
  long size();
}
