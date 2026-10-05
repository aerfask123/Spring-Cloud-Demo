package com.java.springcould.gateway.demo.reactor2;

import com.java.springcould.gateway.demo.listener.MyEventListener;
import com.java.springcould.gateway.demo.processor.MyEventProcessor;
import com.java.springcould.gateway.demo.processor.MyMessageProcessor;
import com.java.springcould.gateway.demo.subscriber.SampleSubscriber;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SignalType;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class FluxTest2 {

    @Test
    public void test1() {
        Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1)).take(5);

        StepVerifier.create(intervalFlux).expectNext(0L, 1L, 2L, 3L, 4L).verifyComplete();
    }

    @Test
    public void test2() {
        Flux.empty().doOnNext(i -> System.out.println("Next!")).doOnComplete(() -> System.out.println("Completed!")).subscribe(System.out::println);
    }

    @Test
    public void test3() {
        Flux.range(1, 6).map(i -> {
            if (i < 4) return i;
            throw new RuntimeException("i can not be greater than 3");
        }).subscribe(System.out::println, error -> System.out.println("Error:" + error.getMessage()));
    }

    @Test
    public void test4() {
        SampleSubscriber<Integer> sampleSubscriber = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 10);
        ints.subscribe(System.out::println,
                error -> System.err.println("Error:" + error),
                () -> System.out.println("Done"),
                s -> s.request(10));
        ints.subscribe(sampleSubscriber);
    }

    @Test
    public void test5() {
        Flux<String> source = Flux.just("a", "b", "c", "d", "e", "f");
        source.map(String::toUpperCase).subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnComplete() {
                System.out.println("Done");
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println(value);
                request(2);
            }

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("Subscribed!");
                request(2);
            }

            @Override
            protected void hookFinally(SignalType type) {
                System.out.println("Finally:" + type);
            }
        });
    }

    @Test
    public void test6() {
        Flux<String> source = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) {
                        sink.complete();
                    }
                    return state + 1;
                }
        );
        source.subscribe(System.out::println);
    }

    @Test
    public void test7() {
        Flux<String> source = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) {
                        sink.complete();
                    }
                    return state;
                }, System.out::println
        );
        source.subscribe(System.out::println);
    }

    @Test
    public void test8() throws InterruptedException {
        MyEventProcessor processor = new MyEventProcessor();
        Flux<String> bridge = Flux.create(sink -> {
            processor.register(new MyEventListener<String>() {

                @Override
                public void onDataChunk(List<String> chunk) {
                    chunk.forEach(sink::next);
                }

                @Override
                public void processComplete() {
                    sink.complete();
                }

                @Override
                public void processError(Throwable e) {
                    sink.error(e);
                }
            });
            sink.onCancel(() -> System.out.println("Flux was cancelled!"));
            sink.onDispose(() -> System.out.println("Flux was disposed!"));
        }, FluxSink.OverflowStrategy.BUFFER);
        bridge.subscribe(System.out::println,
                error -> System.err.println("Error:" + error),
                () -> System.out.println("Stream completed!"));

        Thread.sleep(5000);
    }

    @Test
    public void test9() {
        MyEventProcessor processor = new MyEventProcessor();
        Flux<String> source = Flux.push(sink -> {
            processor.register(new MyEventListener<String>() {

                @Override
                public void onDataChunk(List<String> chunk) {
                    chunk.forEach(sink::next);
                }

                @Override
                public void processComplete() {
                    sink.complete();
                }

                @Override
                public void processError(Throwable e) {
                    sink.error(e);
                }
            });
        }, FluxSink.OverflowStrategy.LATEST);
        source.subscribe(System.out::println);
    }

    @Test
    public void test10() {
        MyMessageProcessor processor = new MyMessageProcessor();
        Flux<String> source = Flux.create(sink -> {
//            processor.register(new MyMessageListener<String>() {
//
//                @Override
//                public void onMessage(List<String> messages) {
//                    messages.forEach(sink::next);
//                }
//            });
            Queue<String> messageQueue = new LinkedList<>();
            sink.onRequest(n -> {
                messageQueue.addAll(processor.request(n));
                if (messageQueue.isEmpty()) {
                    sink.complete();
                    return;
                }
                sink.next(messageQueue.poll());
            });
        }, FluxSink.OverflowStrategy.BUFFER);
        source.log().subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnNext(String value) {
                System.out.println("Received: " + value);

                request(1);
            }

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(10);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Done");
            }
        });
    }

    @Test
    public void test11() {
        Flux<Object> alphabet = Flux.just(13, 9, -1, 20, 30).handle(
                (i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null) {
                        sink.next(letter);
                    }
                }
        ).onErrorReturn(e -> e.getMessage().equals("out of range"), "not in range");

        alphabet.subscribe(System.out::println);
    }

    public String alphabet(int n) {
        if (n < 1 || n > 26) {
            throw new IllegalArgumentException("out of range");
        }
        int letter = 'A' + n - 1;
        return (char) letter + "";
    }

    @Test
    public void test12() {
        AtomicBoolean isDisposed = new AtomicBoolean(false);
        Disposable disposableInstance = new Disposable() {
            @Override
            public void dispose() {
                isDisposed.compareAndSet(false, true);
            }

            @Override
            public boolean isDisposed() {
                return isDisposed.get();
            }

            @Override
            public String toString() {
                return "DISPOSABLE";
            }
        };

        Flux<String> flux = Flux.using(() -> disposableInstance, disposable -> Flux.just(disposable.toString()),
                Disposable::dispose);
        flux.subscribe(System.out::println);
    }

    @Test
    public void test13() {
        LongAdder statsCancel = new LongAdder();

        Flux<String> flux = Flux.just("foo", "bar").doFinally(type -> {
            if (type == SignalType.CANCEL) {
                statsCancel.increment();
            }
        }).take(1);

        flux.subscribe(System.out::println);
        System.out.println(statsCancel.intValue());
    }

    @Test
    public void test14() {
        Flux<String> flux = Flux.interval(Duration.ofMillis(250))
                .<String>handle((input, sink) -> {
                    if (input < 3) {
                        sink.next("tick " + input);
                        return;
                    }
                    sink.error(new RuntimeException("boom"));
                }).onErrorResume(e -> Flux.just("tick " + e));

        flux.subscribe(System.out::println);
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test15() {
        long start = System.currentTimeMillis();
        final AtomicLong last = new AtomicLong();
        Flux.interval(Duration.ofMillis(250)).map(input -> {
            if (input < 3) {
                long inter = System.currentTimeMillis() - start;
                String ret = "tick " + input + " ,time:" + (inter - last.get());
                last.set(inter);
                return ret;
            }
            throw new RuntimeException("boom");
        }).elapsed().retry(2).subscribe(System.out::println, System.err::println);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test16() {
        Flux<String> flux = Flux.<String>error(new RuntimeException("boom"))
                .doOnError(System.out::println).retryWhen(new Retry() {
                    @Override
                    public Publisher<?> generateCompanion(Flux<RetrySignal> retrySignals) {
                        return retrySignals.take(3);
                    }
                });
        flux.subscribe(System.out::println);
    }

    @Test
    public void test17() {
        Flux<String> flux = Flux.<String>error(new RuntimeException("boom"))
                .retryWhen(new Retry() {
                    @Override
                    public Publisher<?> generateCompanion(Flux<RetrySignal> retrySignals) {
                        return retrySignals.zipWith(Flux.range(1, 4), (error, index) -> {
                            if (index < 4) return index;
                            else throw Exceptions.propagate(new RuntimeException("boom index:" + index));
                        });
                    }
                }).checkpoint("desc", true);
        flux.subscribe(System.out::println);
    }

    @Test
    public void test18() {
        // Hooks.onOperatorDebug();
        Flux<String> flux = Flux.range(1, 10)
                .map(i -> {
                    try {
                        return convert(i);
                    } catch (Exception e) {
                        throw Exceptions.propagate(e);
                    }
                });

        flux.subscribe(System.out::println,
                e -> {
                    if (Exceptions.unwrap(e) instanceof IOException) {
                        System.out.println("Something went wrong with I/O");
                    } else {
                        System.out.println("Something went wrong");
                    }
                });
    }

    public String convert(int i) throws IOException {
        if (i > 3) throw new IOException("boom " + i);
        return "OK " + i;
    }
}
