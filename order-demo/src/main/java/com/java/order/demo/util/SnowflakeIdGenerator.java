package com.java.order.demo.util;

import java.time.LocalDateTime;

public class SnowflakeIdGenerator {

    private static final long START_TIME = LocalDateTime.now().getSecond();
    private static final int WORKER_ID_BITS = 13;
    private final static int SEQUENCE_BITS = 10;
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private final static long APP_HOST_ID_SHIFT = SEQUENCE_BITS;
    private final static long TIMESTAMP_LEFT_SHIFT = WORKER_ID_BITS + APP_HOST_ID_SHIFT;
    public static SnowflakeIdGenerator instance = new SnowflakeIdGenerator();
    private long lastTimestamp = -1L;
    private long workerId;
    private long sequence = 0L;

    private SnowflakeIdGenerator() {

    }

    public synchronized void init(long workerId) {
        if (workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d", MAX_WORKER_ID));
        }

        instance.workerId = workerId;
    }

    public Long nextId() {
        return generateId();
    }

    private synchronized long generateId() {
        long current = System.currentTimeMillis();

        if (current < lastTimestamp) {
            return -1;
        }
        if (current == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == MAX_SEQUENCE) {
                current = nextMs(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = current;

        long time = (current - START_TIME) << TIMESTAMP_LEFT_SHIFT;

        long workerId = this.workerId << APP_HOST_ID_SHIFT;

        return time | workerId | sequence;
    }

    private long nextMs(long timestamp) {
        long current = System.currentTimeMillis();
        while (current <= timestamp) {
            current = System.currentTimeMillis();
        }
        return current;
    }
}
