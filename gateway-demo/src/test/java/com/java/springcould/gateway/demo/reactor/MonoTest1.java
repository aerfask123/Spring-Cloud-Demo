package com.java.springcould.gateway.demo.reactor;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;

public class MonoTest1 {

    public static void main(String[] args) {
        Mono.fromSupplier(() -> "Hello World").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello World")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello World")).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object x) {
                System.out.println(x);
            }
        });
    }
}
