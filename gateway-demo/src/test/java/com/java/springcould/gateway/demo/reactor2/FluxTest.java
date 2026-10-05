package com.java.springcould.gateway.demo.reactor2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FluxTest {

    @Test
    public void firstWithSignalFlux() {
        Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth")
                .delaySubscription(Duration.ofMillis(100));
        Flux<String> fastFlux = Flux.just("cheetah", "rabbit", "hare");

        Flux<String> flux = Flux.firstWithSignal(slowFlux, fastFlux);

        StepVerifier.create(flux).expectNext("cheetah", "rabbit", "hare").verifyComplete();
    }

    @Test
    public void skipAFew() {
        Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred").skip(3);

        StepVerifier.create(countFlux).expectNext("ninety nine", "one hundred").verifyComplete();
    }

    @Test
    public void skipAFewSeconds() {
        Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(4));

        StepVerifier.create(countFlux).expectNext("ninety nine", "one hundred").verifyComplete();
    }

    @Test
    public void takeForAwhile() {
        Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
                .delayElements(Duration.ofSeconds(1))
                .take(Duration.ofMillis(3000));

        StepVerifier.create(countFlux).expectNext("one", "two").verifyComplete();
    }

    @Test
    public void flatMap() {

        Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(n -> Flux.just(n).map(p -> {
                    String[] split = p.split("\\s");
                    return new Player(split[0], split[1]);
                }).subscribeOn(Schedulers.parallel()));
        List<Player> players = Arrays.asList(new Player("Michael", "Jordan"), new Player("Scottie", "Pippen"), new Player("Steve", "Kerr"));

        StepVerifier.create(playerFlux).expectNextMatches(p -> players.contains(p))
                .expectNextMatches(p -> players.contains(p))
                .expectNextMatches(p -> players.contains(p))
                .verifyComplete();
    }

    @Test
    public void bufferAndFlatMap() throws Exception {
        Flux.just("apple", "orange", "banana", "peach", "cherry")
                .buffer(3).flatMap(f -> Flux.fromIterable(f)
                        .map(String::toUpperCase).subscribeOn(Schedulers.parallel()).log()
                ).subscribe();
    }

    @Test
    public void collecList() {
        Flux<String> fruitFlux = Flux.just("apple", "orange", "banana", "peach", "cherry");

        Mono<List<String>> fruitListMono = fruitFlux.collectList();

        StepVerifier.create(fruitListMono).expectNext(Arrays.asList("apple", "orange", "banana", "peach", "cherry"))
                .verifyComplete();
    }

    @Test
    public void collectMap() {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        Mono<Map<Character, String>> animalMapMono = animalFlux.collectMap(a -> a.charAt(0));

        StepVerifier.create(animalMapMono).expectNextMatches(m -> {
            return m.size() == 3 && m.get('a').equals("aardvark") && m.get('e').equals("eagle") && m.get('k').equals("kangaroo");
        }).verifyComplete();
    }

    private record Player(String firstName, String lastName) {
    }
}
