package com.java.order.demo;

import com.java.order.demo.locks.ZKLock;
import com.java.order.demo.util.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ZKLockTest {

    int count = 0;

    @Test
    public void testLock() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                ZKLock lock = new ZKLock();
                lock.lock();
                for (int j = 0; j < 10; j++) {
                    count++;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("count:{}", count);
                lock.unlock();
            });
        }
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    @Test
    public void testZKMutex() {
        CuratorFramework client = ZKClient.instance.getClient();
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        final InterProcessMutex zkMutex = new InterProcessMutex(client, "/zk-mutex");

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    zkMutex.acquire();

                    for (int j = 0; j < 10; j++) {
                        count++;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("count:{}", count);

                    zkMutex.release();

                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("COUNT:{}", count);

        executorService.shutdown();
    }
}
