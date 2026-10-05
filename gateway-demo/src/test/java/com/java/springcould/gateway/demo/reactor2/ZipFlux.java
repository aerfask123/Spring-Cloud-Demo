package com.java.springcould.gateway.demo.reactor2;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

public class ZipFlux {
    public static void main(String[] args) {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");
        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);

        StepVerifier.create(zippedFlux).expectNextMatches(tuple -> tuple.getT1().equals("Garfield") && tuple.getT2().equals("Lasagna"))
                .expectNextMatches(tuple -> tuple.getT1().equals("Kojak") && tuple.getT2().equals("Lollipops"))
                .expectNextMatches(tuple -> tuple.getT1().equals("Barbossa") && tuple.getT2().equals("Apples"))
                .verifyComplete();

        Flux<String> zippedFlux2 = Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);

        StepVerifier.create(zippedFlux2).expectNext("Garfield eats Lasagna", "Kojak eats Lollipops", "Barbossa eats Apples").verifyComplete();
    }
}
