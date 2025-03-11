package com.github.kingschan1204.easycrawl.schedule.pool;

import com.github.kingschan1204.easycrawl.schedule.TaskThreadFactory;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kingschan 2024-12-28
 */
@Slf4j
public class PausableScheduledThreadPool extends ScheduledThreadPoolExecutor {
  private final String taskName;
  private boolean isPaused;
  private final Lock pauseLock = new ReentrantLock();
  private final Condition unpaused = pauseLock.newCondition();

  public PausableScheduledThreadPool(String taskName, int corePoolSize) {
    super(corePoolSize);
    int core = Runtime.getRuntime().availableProcessors();
    // 设置最大线程数
    setMaximumPoolSize(core * 2);
    setCorePoolSize(corePoolSize);
    // 设置空闲线程的存活时间(当线程池中的线程数超过 corePoolSize 时)
    setKeepAliveTime(1, TimeUnit.SECONDS);
    // 取消任务时从队列中移除任务 默认为false
    setRemoveOnCancelPolicy(true);
    setThreadFactory(new TaskThreadFactory(taskName));
    this.taskName = taskName;
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    super.beforeExecute(t, r);
    pauseLock.lock();
    try {
      while (isPaused) {
        unpaused.await();
      }
    } catch (InterruptedException ie) {
      t.interrupt();
    } finally {
      pauseLock.unlock();
    }
  }

  public void pause() {
    log.warn("---------暂停任务 {}---------", taskName);
    pauseLock.lock();
    try {
      isPaused = true;
    } finally {
      pauseLock.unlock();
    }
  }

  public void resume() {
    log.warn("---------恢复任务 {}---------", taskName);
    pauseLock.lock();
    try {
      isPaused = false;
      unpaused.signalAll();
    } finally {
      pauseLock.unlock();
    }
  }

  @Override
  public void shutdown() {
    super.shutdown();
    log.info("任务：{} 执行完成后将关闭释放资源！", taskName);
  }
}
