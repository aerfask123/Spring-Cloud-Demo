package com.java.springcould.gateway.demo.reactor;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Iterator;

public class FLuxTest7 {

    public static void main(String[] args) {
        FLuxTest7 test = new FLuxTest7();
        test.fluxExample();
    }

    public void fluxExample() {
        Iterator<Integer> it = Arrays.asList(1, 2, 3, 4, 5).iterator();

        Flux<Integer> iteratorFlux = Flux.create(sink -> {
            while (it.hasNext()) {
                sink.next(it.next());
            }
            sink.complete();
        });

        iteratorFlux.log().subscribe(System.out::println);
    }
}
