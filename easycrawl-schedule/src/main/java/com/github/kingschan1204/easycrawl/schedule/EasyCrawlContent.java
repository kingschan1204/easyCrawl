package com.github.kingschan1204.easycrawl.schedule;

import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import com.github.kingschan1204.easycrawl.schedule.pool.TaskSchedule;
import com.github.kingschan1204.easycrawl.schedule.pool.impl.EasyCrawlScheduledPool;
import com.github.kingschan1204.easycrawl.schedule.queue.RedisQueue;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EasyCrawlContent {
  private static ConcurrentHashMap<String, TaskSchedule> threadContent;

  static {
    threadContent = new ConcurrentHashMap<>();
  }

  private EasyCrawlContent() {}

  private static class Holder {
    private static EasyCrawlContent instance = new EasyCrawlContent();
  }

  public static EasyCrawlContent getInstance() {
    return Holder.instance;
  }

  public void scheduleByRedisQueue(
      String taskName,
      RedisQueue queue,
      Runnable command,
      int runQuantity,
      LocalDateTime time,
      long delay) {
    featureTime(time);
    validateTaskName(taskName);
    long delaySeconds = DateHelper.getMillisDiff(LocalDateTime.now(), time).toSeconds();
    EasyCrawlScheduledPool pool = new EasyCrawlScheduledPool(taskName, runQuantity);
    for (int i = 0; i < runQuantity; i++) {
      pool.scheduleByRedisQueue(queue, command, delaySeconds, delay, TimeUnit.SECONDS);
    }
    threadContent.put(taskName, pool);
  }

  /**
   * 根据redis队列执行任务，完成后自动关闭线程池
   *
   * @param taskName 任务名称
   * @param queue redis队列
   * @param command 任务
   * @param runQuantity 并发线程数量
   * @param initialDelay 任务第一次执行前的延迟时间
   * @param delay 每次执行任务之间的延迟时间，从上一次任务结束到下一次任务开始的时间间隔
   * @param unit 时间单位
   */
  public void scheduleByRedisQueue(
      String taskName,
      RedisQueue queue,
      Runnable command,
      int runQuantity,
      long initialDelay,
      long delay,
      TimeUnit unit) {
    validateTaskName(taskName);
    EasyCrawlScheduledPool pool = new EasyCrawlScheduledPool(taskName, runQuantity);
    for (int i = 0; i < runQuantity; i++) {
      pool.scheduleByRedisQueue(queue, command, initialDelay, delay, unit);
    }
    threadContent.put(taskName, pool);
  }

  public void scheduleByQueue(
      String taskName,
      BlockingQueue queue,
      Runnable command,
      int runQuantity,
      long initialDelay,
      long delay,
      TimeUnit unit) {
    validateTaskName(taskName);
    EasyCrawlScheduledPool pool = new EasyCrawlScheduledPool(taskName, runQuantity);
    for (int i = 0; i < runQuantity; i++) {
      pool.scheduleByQueue(queue, command, initialDelay, delay, unit);
    }
    threadContent.put(taskName, pool);
  }

  /**
   * 调度一次 立马执行
   *
   * @param taskName
   * @param command
   */
  public void schedulingOnce(String taskName, Runnable command) {
    this.schedulingOnce(taskName, command, 0, TimeUnit.SECONDS);
  }

  /**
   * 调度一次，指定时间执行
   *
   * @param taskName
   * @param command
   * @param time
   */
  public void schedulingOnce(String taskName, Runnable command, LocalDateTime time) {
    featureTime(time);
    validateTaskName(taskName);
    EasyCrawlScheduledPool pool = new EasyCrawlScheduledPool(taskName, 1);
    long delay = DateHelper.getMillisDiff(LocalDateTime.now(), time).toSeconds();
    pool.schedule(command, delay, TimeUnit.SECONDS);
    pool.shutdown();
  }

  /**
   * 调度一次，指定时间单位延迟执行
   *
   * @param taskName
   * @param command
   * @param delay
   * @param unit
   */
  public void schedulingOnce(String taskName, Runnable command, long delay, TimeUnit unit) {
    validateTaskName(taskName);
    EasyCrawlScheduledPool pool = new EasyCrawlScheduledPool(taskName, 1);
    pool.schedule(command, delay, unit);
    pool.shutdown();
  }

  private void featureTime(LocalDateTime time) {
    LocalDateTime now = LocalDateTime.now();
    if (now.isAfter(time)) {
      throw new RuntimeException("时间已过！");
    }
  }

  private void validateTaskName(String taskName) {
    if (threadContent.containsKey(taskName)) {
      if (threadContent.get(taskName).isTerminated()) {
        threadContent.remove(taskName);
      } else {
        throw new RuntimeException("任务已存在，请忽重复创建！");
      }
    }
  }

  public TaskSchedule get(String taskName) {
    if (!threadContent.containsKey(taskName)) {
      throw new RuntimeException("任务不存在！");
    }
    return threadContent.get(taskName);
  }

  public void remove(String taskName) {
    if (!threadContent.containsKey(taskName)) {
      throw new RuntimeException("任务不存在！");
    }
    if (!threadContent.get(taskName).isTerminated()) {
      throw new RuntimeException("任务未执行完毕，无法删除！");
    }
    log.info("任务：{} 执行完成,已从容器中删除！", taskName);
    threadContent.remove(taskName);
  }

  public void pauseAll() {
    threadContent.forEach(
        (k, v) -> {
          v.pause();
        });
  }

  public void resumeAll() {
    threadContent.forEach(
        (k, v) -> {
          v.resume();
        });
  }
}
