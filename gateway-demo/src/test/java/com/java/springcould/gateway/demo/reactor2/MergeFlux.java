package com.java.springcould.gateway.demo.reactor2;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class MergeFlux {

    public static void main(String[] args) {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500));
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
                .delaySubscription(Duration.ofMillis(250)).delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);

        StepVerifier.create(mergedFlux).expectNext("Garfield", "Lasagna", "Kojak", "Lollipops", "Barbossa", "Apples").verifyComplete();
    }
}
