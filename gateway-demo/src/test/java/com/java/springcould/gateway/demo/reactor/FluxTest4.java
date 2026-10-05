package com.java.springcould.gateway.demo.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Consumer;

public class FluxTest4 {

    public static void main(String[] args) {
        Flux.range(1, 100).buffer(20).subscribe(System.out::println);

        Flux.interval(Duration.ofMillis(100)).buffer(Duration.ofMillis(1001)).take(2).toStream()
                .forEach(System.out::println);
        System.out.println("------------------------");
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);
        System.out.println("------------------------");
        Flux.just("a", "b").zipWith(Flux.just("c", "d")).subscribe(System.out::println);
        Flux.just("a", "b").zipWith(Flux.just("c", "d"),
                (s1, s2) -> String.format("%s->%s", s1, s2)).subscribe(System.out::println);
        System.out.println("------------------------");

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.printf("%d\t", integer);
            }
        };

        Flux.range(1, 1000).take(10).subscribe(consumer);
        System.out.println();
        Flux.range(1, 1000).takeLast(10).subscribe(consumer);
        System.out.println();
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(consumer);
        System.out.println();
        Flux.range(1, 1000).takeUntil(i -> i > 10).subscribe(consumer);
        System.out.println();
        System.out.println("------------------------");

        Flux.range(1, 100).reduce((a, b) -> a + b).subscribe(System.out::println);
        Flux.range(1, 100).reduce(100, (a, b) -> a + b).subscribe(System.out::println);

        Flux.just(5, 10).flatMap(i -> Flux.interval(Duration.ofMillis(i * 10),
                Duration.ofMillis(100)).take(i)).toStream().forEach(System.out::println);
    }
}
