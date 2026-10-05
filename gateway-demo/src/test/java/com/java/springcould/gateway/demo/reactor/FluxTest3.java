package com.java.springcould.gateway.demo.reactor;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class FluxTest3 {

    public static void main(String[] args) throws IOException {
        Flux.just("Hello", "World").subscribe(System.out::println);
        Flux.fromArray(new Integer[]{1, 2, 3, 4, 5}).subscribe(System.out::println);
        Flux.empty().subscribe(System.out::println);
        Flux.range(1, 5).subscribe(System.out::println);
        Flux.never().subscribe(System.out::println);
        Flux.interval(Duration.of(10, ChronoUnit.MILLIS)).subscribe(System.out::println);

        Flux.generate(sink -> {
            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        Flux<Integer> generate = Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        });
        generate.subscribe(System.out::println);

        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        });
    }
}
