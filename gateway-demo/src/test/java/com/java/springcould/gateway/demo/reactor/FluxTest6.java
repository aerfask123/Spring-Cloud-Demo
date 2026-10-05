package com.java.springcould.gateway.demo.reactor;

import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.function.Consumer;

public class FluxTest6 {

    public static void main(String[] args) throws InterruptedException {
        Mono<Date> m1 = Mono.just(new Date());
        Mono<Date> m2 = Mono.defer(() -> Mono.just(new Date()));
        m1.subscribe(new Consumer<Date>() {
            @Override
            public void accept(Date x) {
                System.out.println(x);
            }
        });
        m2.subscribe(System.out::println);
        Thread.sleep(5000);
        m1.subscribe(System.out::println);
        m2.subscribe(System.out::println);
    }
}
