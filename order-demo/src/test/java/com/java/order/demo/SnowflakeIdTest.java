package com.java.order.demo;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.java.order.demo.util.SnowFlakeIdWorker;
import com.java.order.demo.util.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SnowflakeIdTest {

    @Test
    public void test() {
        long workId = SnowFlakeIdWorker.instance.getNodeId();
        SnowflakeIdGenerator.instance.init(workId);

        ExecutorService es = Executors.newFixedThreadPool(10);

        final ConcurrentHashSet idSet = new ConcurrentHashSet();

        Collections.synchronizedCollection(idSet);
        long start = System.currentTimeMillis();
        log.info("start time: {}", start);
        for (int i = 0; i < 10; i++) {
            es.execute(() -> {
                for (int j = 0; j < 5000000; j++) {
                    idSet.add(SnowflakeIdGenerator.instance.nextId());
                }
            });
        }

        es.shutdown();

        try {
            es.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        log.info("idSet size: {}", idSet.size());
        log.info("耗时：{} ms", end - start);
    }
}
