package com.java.springcould.gateway.demo.reactor2;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.PublisherProbe;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class FluxTest3 {

    @Test
    public void testAppendBoomError() {
        Flux<String> source = Flux.just("foo", "bar");

        StepVerifier.create(appendBoomError(source))
                .expectNext("foo").expectNext("bar").expectErrorMessage("boom").verify();
    }

    public <T> Flux<T> appendBoomError(Flux<T> source) {
        return source.concatWith(Mono.error(new IllegalArgumentException("boom")));
    }

    @Test
    public void testVirtualTime() {
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
                .expectSubscription().expectNoEvent(Duration.ofDays(1))
                .expectNext(0L).verifyComplete();
    }

    @Test
    public void testContext() {
        StepVerifier.create(Mono.just(1).map(i -> i + 10), StepVerifierOptions.create().withInitialContext(Context.of("foo", "bar")))
                .expectAccessibleContext().contains("foo", "bar").then().expectNext(11).verifyComplete();
    }

    @Test
    public void testSplitPathIsUsed() {
        StepVerifier.create(processOrCallback(Mono.just("just a phrase with    tabe!"),
                Mono.just("EMPTY_PHRASE"))).expectNext("just", "a", "phrase", "with", "tabe!").verifyComplete();
    }

    @Test
    public void testEmptyPathIsUsed() {
        StepVerifier.create(processOrCallback(Mono.empty(), Mono.just("EMPTY_PHRASE")))
                .expectNext("EMPTY_PHRASE").verifyComplete();
    }

    public Flux<String> processOrCallback(Mono<String> source, Publisher<String> fallback) {
        return source.flatMapMany(phrase -> Flux.fromArray(phrase.split("\\s+")))
                .switchIfEmpty(fallback);
    }

    @Test
    public void testCommandEmptyPathIsUsed() {
        PublisherProbe<Void> probe = PublisherProbe.empty();

        StepVerifier.create(processOrFallback(Mono.empty(), probe.mono())).verifyComplete();
        probe.assertWasSubscribed();
        probe.assertWasRequested();
        probe.assertWasNotCancelled();
    }

    private Mono<String> executeCommand(String command) {
        return Mono.just(command + " DONE");
    }

    public Mono<Void> processOrFallback(Mono<String> commandSource, Mono<Void> doWhenEmpty) {
        return commandSource.flatMap(command -> executeCommand(command).then()).switchIfEmpty(doWhenEmpty);
    }

    @Test
    public void testTransform() {
        Function<Flux<String>, Flux<String>> filterAndMap =
                f -> f.filter(color -> !color.equals("orange")).map(String::toUpperCase);

        Flux.fromIterable(Arrays.asList("blue", "red", "green", "yellow", "orange", "purple"))
                .doOnNext(System.out::println).transform(filterAndMap)
                .doOnComplete(() -> System.out.println("Done"))
                .subscribe(d -> System.out.println("Subscriber to Transformed MapAndFilter: " + d));
    }

    @Test
    public void testCompose() {
        AtomicInteger ai = new AtomicInteger();
        Function<Flux<String>, Flux<String>> filterAndMap = f -> {
            if (ai.incrementAndGet() % 2 == 1) {
                return f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);
            }
            return f.filter(color -> !color.equals("purple")).map(String::toUpperCase);
        };

        Flux<String> composedFlux = Flux.fromIterable(Arrays.asList("blue", "red", "green", "yellow", "orange", "purple"))
                .doOnNext(System.out::println).transformDeferred(filterAndMap);

        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Deferred FilterAndMap: " + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Deferred FilterAndMap: " + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 3 to Deferred FilterAndMap: " + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 4 to Deferred FilterAndMap: " + d));
    }

    @Test
    public void testFromIterable() {
        Flux<String> source = Flux.fromIterable(Arrays.asList("blue", "red", "green", "yellow", "orange", "purple"))
                .doOnNext(System.out::println)
                .filter(color -> color.startsWith("o"))
                .map(String::toUpperCase);
        Hooks.onOperatorDebug();
        source.subscribe(d -> System.out.println("Subscriber 1 to Deferred FilterAndMap: " + d));
        Hooks.resetOnOperatorDebug();
        source.subscribe(d -> System.out.println("Subscriber 2 to Deferred FilterAndMap: " + d));
    }

    @Test
    public void testPublish() {
        Sinks.Many<String> hotSource = Sinks.many().multicast().onBackpressureBuffer();

        Flux<String> hotFlux = hotSource.asFlux().map(String::toUpperCase).log();

        hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Deferred FilterAndMap: " + d));

        hotSource.tryEmitNext("blue");
        hotSource.tryEmitNext("red");

        hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Deferred FilterAndMap: " + d));

        hotSource.tryEmitNext("green");
        hotSource.tryEmitNext("yellow");
        hotSource.tryEmitComplete();
    }

    @Test
    public void testConnectableFlux() {
        Flux<Integer> source = Flux.range(1, 3).doOnSubscribe(s -> System.out.println("Subscriber to source")).log();

        ConnectableFlux<Integer> co = source.publish();

        co.subscribe(System.out::println, e -> e.printStackTrace(), () -> System.out.println("Done"));
        co.subscribe(System.out::println, e -> e.printStackTrace(), () -> System.out.println("Done"));

        System.out.println("Done subscribing");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("will now connect");

        co.connect();
    }

    @Test
    public void testConnectableFlux2() throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 3).doOnSubscribe(s -> System.out.println("Subscriber to source")).log();

        Flux<Integer> co = source.publish().autoConnect(2);

        co.subscribe(System.out::println, e -> e.printStackTrace(), () -> System.out.println("Done"));
        System.out.println("first subscribed");
        Thread.sleep(500);
        System.out.println("second subscribed");
        co.subscribe(System.out::println, e -> e.printStackTrace(), () -> System.out.println("Done"));
    }

    @Test
    public void testGroupBy() {
        Flux<String> flux = Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even" : "odd")
                .concatMap(g -> g.defaultIfEmpty(-1)
                        .map(String::valueOf).startWith(g.key()));

        StepVerifier.create(flux).expectNext("odd", "1", "3", "5", "11", "13")
                .expectNext("even", "2", "4", "6", "12").verifyComplete();
    }

    @Test
    public void testWindow1() {
        Flux<Integer> flux = Flux.range(1, 10).window(5, 3).concatMap(g -> g.defaultIfEmpty(-1));

        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5).expectNext(4, 5, 6, 7, 8).expectNext(7, 8, 9, 10).expectNext(10).verifyComplete();
    }

    @Test
    public void testWindow2() {
        Flux<Integer> flux = Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13, 15).windowWhile(i -> i % 2 == 0)
                .concatMap(g -> g.defaultIfEmpty(-1));
        StepVerifier.create(flux).expectNext(-1, -1, -1).expectNext(2, 4, 6)
                .expectNext(12).expectNext(-1).verifyComplete();
    }

    @Test
    public void testParallelFlux() {
        Flux.range(1, 10).parallel(2).subscribe(i -> System.out.println(Thread.currentThread().getName() + "->" + i));
    }


    @Test
    public void testParallelFlux2() {
        Flux.range(1, 30).parallel().runOn(Schedulers.boundedElastic())
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + "->" + i));
    }

    @Test
    public void testContext2() {
        String key = "message";
        Mono<String> mono = Mono.just("Hello").flatMap(s -> Mono.deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key))))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(mono).expectNext("Hello World").verifyComplete();
    }

    @Test
    public void testContext3() {
        String key = "message";
        Mono<String> mono = Mono.just("Hello").flatMap(s -> Mono.deferContextual(ctxView ->
                Mono.just(s + " " + ctxView.getOrDefault(key, "Stranger")))).contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(mono).expectNext("Hello World").verifyComplete();
    }

    @Test
    public void testContext4() {
        String key = "message";
        Mono<String> mono = Mono.deferContextual(ctxView -> Mono.just(ctxView.getOrDefault(key, "Default")))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(mono).expectNext("World").verifyComplete();
    }
}
