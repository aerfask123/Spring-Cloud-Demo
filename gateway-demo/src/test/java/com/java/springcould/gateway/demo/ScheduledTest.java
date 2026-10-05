package com.java.springcould.gateway.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTest {

    public static void main(String[] args) {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
        threadPool.scheduleAtFixedRate(() -> {
            System.out.println("Scheduled task executed");
        }, 5, 60, TimeUnit.SECONDS);
    }
}
