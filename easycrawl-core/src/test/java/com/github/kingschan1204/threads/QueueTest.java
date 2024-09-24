package com.github.kingschan1204.threads;

import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class QueueTest {

    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        Runnable producer = () ->{
            try {
                queue.put(DateHelper.now().dateTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable producer1 = () ->{
            try {
                queue.put("1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable consumer = () ->{
            try {
                while (true) {
                    String time = queue.take();
                    System.out.println("Consumer " + time);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        executorService.scheduleAtFixedRate(producer,1,5,TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(producer1,1,1,TimeUnit.SECONDS);
        executorService.submit(consumer);
    }

}
