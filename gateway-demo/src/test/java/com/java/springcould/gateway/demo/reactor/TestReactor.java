package com.java.springcould.gateway.demo.reactor;


import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TestReactor {

    public static void main(String[] args) {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);

        integerFlux.subscribe(System.out::println);

        Flux<String> stringFlux = Flux.just("a", "b", "c", "d", "e");
        stringFlux.subscribe(System.out::println);

        Integer[] array = new Integer[]{1, 2, 3, 4, 5};
        Flux.fromArray(array).subscribe(System.out::println);

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Flux.fromIterable(list).subscribe(System.out::println);

        Stream<Integer> stream = list.stream();
        Flux.fromStream(stream).subscribe(System.out::println);
    }
}
