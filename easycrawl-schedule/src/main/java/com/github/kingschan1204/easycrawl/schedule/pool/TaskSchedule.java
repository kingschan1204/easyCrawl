package com.github.kingschan1204.easycrawl.schedule.pool;

import com.github.kingschan1204.easycrawl.schedule.queue.RedisQueue;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author kingschan
 */
public interface TaskSchedule {
  String taskName();

  long taskCount();

  long activeCount();

  long completedTaskCount();

  long queueSize();

  long errorCount();

  String nextRunTime();

  /**
   * 检查线程池是否已经完全终止 true：线程池已经终止 false：线程池尚未终止
   *
   * @return
   */
  boolean isTerminated();

  void pause();

  void resume();

  void shutdown();

  boolean awaitTermination(long time, TimeUnit timeUnit);

  ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit);

  ScheduledFuture scheduleWithFixedDelay(
      Runnable command, long initialDelay, long delay, TimeUnit unit);

  ScheduledFuture scheduleByQueue(
      BlockingQueue<Map<String, Object>> queue,
      Runnable command,
      long initialDelay,
      long delay,
      TimeUnit unit);

  /**
   * 根据redis队列执行任务，完成后自动关闭线程池
   *
   * @param queue redis队列
   * @param command 任务
   * @param initialDelay 任务第一次执行前的延迟时间
   * @param delay 每次执行任务之间的延迟时间，从上一次任务结束到下一次任务开始的时间间隔
   * @param unit 时间单位
   * @return
   */
  ScheduledFuture scheduleByRedisQueue(
      RedisQueue queue, Runnable command, long initialDelay, long delay, TimeUnit unit);
}
