package com.java.springcould.gateway.demo.reactor;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.function.Consumer;


public class FluxTest1 {

    public static void main(String[] args) throws IOException {
        Mono.error(new IllegalStateException("error"));
        //  Flux.interval(Duration.ofMillis(100)).map(i -> i + 10).take(10).subscribe(System.out::println);
        Flux.just("Ben", "Michael", "Mark").doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("Hello " + s);
            }
        }).doOnComplete(new Runnable() {
            @Override
            public void run() {
                System.out.println("Completed");
            }
        }).subscribe();
        System.in.read();
    }
}
