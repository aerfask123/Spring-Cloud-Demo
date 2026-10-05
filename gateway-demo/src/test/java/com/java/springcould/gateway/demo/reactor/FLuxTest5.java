package com.java.springcould.gateway.demo.reactor;

import reactor.core.publisher.Flux;

public class FLuxTest5 {

    public static void main(String[] args) {
        Flux.just(1, 2, 3).map(i -> 1 / i).checkpoint("test").subscribe(x ->
        {
            int y = x * 10 + 5;
            System.out.println(y);
        });
//        Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
//        Mono.justOrEmpty(Optional.empty()).subscribe(System.out::println);
//        Mono.create(sink -> sink.success("Hello World")).subscribe(System.out::println);
    }
}
