package com.github.kingschan1204.easycrawl.schedule.pool.impl;

import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import com.github.kingschan1204.easycrawl.schedule.pool.PausableScheduledThreadPool;
import com.github.kingschan1204.easycrawl.schedule.pool.TaskSchedule;
import com.github.kingschan1204.easycrawl.schedule.queue.RedisQueue;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kingschan 2025-02-25
 */
@Slf4j
public class EasyCrawlScheduledPool implements TaskSchedule {
  final String taskName;

  /** 错误次数 */
  final AtomicInteger errorCount;

  /** 最大错误次数 */
  Integer maxErrorCount;

  /** 记录开始时间 */
  final LocalDateTime startTime;

  private PausableScheduledThreadPool threadPool;

  //  boolean isShutdown = false;

  ReentrantLock lock = new ReentrantLock();

  public EasyCrawlScheduledPool(String taskName, int corePoolSize) {
    this.taskName = taskName;
    this.threadPool = new PausableScheduledThreadPool(taskName, corePoolSize);
    this.errorCount = new AtomicInteger(0);
    this.startTime = LocalDateTime.now();
  }

  @Override
  public String taskName() {
    return this.taskName;
  }

  @Override
  public long taskCount() {
    return threadPool.getTaskCount();
  }

  @Override
  public long activeCount() {
    return threadPool.getActiveCount();
  }

  @Override
  public long completedTaskCount() {
    return threadPool.getCompletedTaskCount();
  }

  @Override
  public long queueSize() {
    return threadPool.getQueue().size();
  }

  @Override
  public long errorCount() {
    return this.errorCount.get();
  }

  @Override
  public String nextRunTime() {
    return null;
  }

  @Override
  public boolean isTerminated() {
    return threadPool.isTerminated();
  }

  @Override
  public void pause() {
    threadPool.pause();
  }

  @Override
  public void resume() {
    threadPool.resume();
  }

  @Override
  public void shutdown() {
    threadPool.shutdown();
  }

  @Override
  public boolean awaitTermination(long time, TimeUnit timeUnit) {
    try {
      return threadPool.awaitTermination(time, timeUnit);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
    return threadPool.schedule(command, delay, unit);
  }

  @Override
  public ScheduledFuture scheduleWithFixedDelay(
      Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return threadPool.scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }

  @Override
  public ScheduledFuture scheduleByQueue(
      BlockingQueue<Map<String, Object>> queue,
      Runnable command,
      long initialDelay,
      long delay,
      TimeUnit unit) {
    return threadPool.scheduleWithFixedDelay(
        () -> {
          if (queue.size() > 0) {
            command.run();
          } else {
            threadPool.shutdown();
            log.info("任务：{} 队列执行完毕，关闭任务！", taskName);
          }
        },
        initialDelay,
        delay,
        unit);
  }

  @Override
  public ScheduledFuture scheduleByRedisQueue(
      RedisQueue queue, Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return threadPool.scheduleWithFixedDelay(
        () -> {
          if (queue.size() > 0) {
            command.run();
          } else {
            if (lock.tryLock()) {
              //              if (!isShutdown) {
              try {
                threadPool.shutdown();
                long runTime =
                    DateHelper.getMillisDiff(this.startTime, LocalDateTime.now()).toSeconds();
                log.info(
                    "任务：{} redis list队列: {} 执行完毕，关闭任务！执行总时长：{}秒",
                    taskName,
                    queue.redisKey(),
                    runTime);
                /* if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                  // 如果超时未完成，强制关闭
                  log.warn("任务：{} 关闭释放资源超时，强制关闭！", taskName);
                  threadPool.shutdownNow();
                } else {
                  log.info("任务：{} 释放资源成功！", taskName);
                  EasyCrawlContent.getInstance().remove(taskName);
                }*/
                //                isShutdown = true;
                //                  EasyCrawlContent.getInstance().remove(taskName);
              } catch (Exception e) {
                e.printStackTrace();
              } finally {
                lock.unlock();
              }
              //              }
            } else {
              log.info("没有获取到锁...");
            }
          }
        },
        initialDelay,
        delay,
        unit);
  }
}
