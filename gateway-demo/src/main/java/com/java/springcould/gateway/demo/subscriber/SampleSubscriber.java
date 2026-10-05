package com.java.springcould.gateway.demo.subscriber;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class SampleSubscriber<T> extends BaseSubscriber<T> {

    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("hookOnSubscribe");
        request(3);
    }

    public void hookOnNext(T t) {
        System.out.println("hookOnNext:" + t);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        request(1);
    }
}
