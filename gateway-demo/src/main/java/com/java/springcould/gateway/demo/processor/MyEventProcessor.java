package com.java.springcould.gateway.demo.processor;


import com.java.springcould.gateway.demo.listener.MyEventListener;

import java.util.List;

public class MyEventProcessor {

    private MyEventListener<String> myEventListener;

    public void register(MyEventListener<String> listener) {
        this.myEventListener = listener;
        startProducing();
    }

    public void startProducing() {
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                List<String> chunk = List.of("Data-" + i + "-A", "Data-" + i + "-B");

                if (myEventListener != null) {
                    myEventListener.onDataChunk(chunk);
                }
            }

            if (myEventListener != null) {
                myEventListener.processComplete();
            }
        } catch (InterruptedException e) {
            if (myEventListener != null) {
                myEventListener.processError(e);
            }
        }
    }
}
