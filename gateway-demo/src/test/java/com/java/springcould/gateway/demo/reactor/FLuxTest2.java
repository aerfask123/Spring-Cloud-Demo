package com.java.springcould.gateway.demo.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.List;

public class FLuxTest2 {

    public static void main(String[] args) {
        Flux.just("Ben", "Michael", "Mark").subscribe(new Subscriber<String>() {

            @Override
            public void onSubscribe(Subscription s) {
                s.request(3);
            }

            @Override
            public void onNext(String s) {
                System.out.println("Hello " + s);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error" + t);
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        });

        List<String> list = Flux.just("Ben", "Michael", "Mark").collectList().block();
        System.out.println(list);
    }
}
