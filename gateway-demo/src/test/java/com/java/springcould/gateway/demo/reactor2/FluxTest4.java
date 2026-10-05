package com.java.springcould.gateway.demo.reactor2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class FluxTest4 {

    @Test
    public void testfromSupplier() {
        Mono<String> mono = Mono.fromSupplier(() -> {
            System.out.println("Supplier run!");
            return "World";
        });
        mono.subscribe(System.out::println);
        mono.subscribe(System.out::println);
    }

    @Test
    public void testFromRunnable() {
        Mono<String> mono = Mono.fromRunnable(() -> {
            System.out.println("Runnable run!");
        });

        mono.doOnSuccess(val -> System.out.println("Terminated!")).subscribe(System.out::println);
        mono.subscribe(System.out::println);

        Mono<String> mo = Mono.just(getData());
        mo.subscribe(System.out::println);
        mo.subscribe(System.out::println);
    }

    private String getData() {
        System.out.println("getData");
        return "World";
    }

    @Test
    public void testFromCallable() {
        Mono<String> mono = Mono.fromCallable(() -> {
            System.out.println("Callable run!");
            return "World";
        });

        mono.subscribe(System.out::println);
    }

    @Test
    public void testFromFuture() {
        Mono<String> mono = Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            System.out.println("Future run!");
            return "World";
        }));

        mono.subscribe(System.out::println);
    }
}
