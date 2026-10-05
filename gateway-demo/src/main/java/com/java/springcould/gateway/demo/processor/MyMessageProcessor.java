package com.java.springcould.gateway.demo.processor;

import com.java.springcould.gateway.demo.listener.MyMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyMessageProcessor {
    private final AtomicInteger counter = new AtomicInteger(0);

    private final int MAX_MESSAGE_COUNT = 1000;

    private MyMessageListener<String> listener;

    public void register(MyMessageListener<String> listener) {
        this.listener = listener;

        startProducing();
    }

    public void startProducing() {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<String> chunk = List.of("Data-" + i + "-A", "Data-" + i + "-B");

            if (listener != null) {
                listener.onMessage(chunk);
            }
        }
    }

    public List<String> request(long n) {
        List<String> messages = new ArrayList<>();
        int remaining = MAX_MESSAGE_COUNT - counter.get();
        if (remaining <= 0) {
            return messages;
        }
        long loops = Math.min(n, remaining);

        for (int i = 0; i < loops; i++) {
            String message = "Data-" + counter.getAndIncrement() + "-A";
            messages.add(message);
        }
        return messages;
    }
}
