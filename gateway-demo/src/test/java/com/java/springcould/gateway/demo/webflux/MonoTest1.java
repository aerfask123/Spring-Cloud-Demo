package com.java.springcould.gateway.demo.webflux;

import com.google.common.util.concurrent.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class MonoTest1 {

    ExecutorService bizPool = Executors.newFixedThreadPool(10);

    @Test
    public void callbackExample() throws InterruptedException {
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(bizPool);
        ListenableFuture future = executor.submit(() -> {
            log.info("mydebug run, " + Thread.currentThread().getName());
        });

        Futures.addCallback(future, new FutureCallback() {
            @Override
            public void onSuccess(Object result) {
                log.info("mydebug onSuccess," + Thread.currentThread().getName());
            }

            @Override
            public void onFailure(Throwable t) {
                log.info("mydebug onFailure," + Thread.currentThread().getName());
            }
        }, bizPool);
        Thread.sleep(1000);
    }

    @Test
    public void monoCreateExample() throws InterruptedException {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(bizPool);
        ListenableFuture future = service.submit(() -> {
            log.info("1: run," + Thread.currentThread().getName());
            return "async out";
        });

        Mono responseMono = Mono.create(new Consumer<MonoSink<Object>>() {
            @Override
            public void accept(MonoSink<Object> sink) {
                CallbackHandlerInner callbackHandlerInner = new CallbackHandlerInner(sink);
                Futures.addCallback(future, callbackHandlerInner.getResponseCallback(), service);
            }
        });

        responseMono.subscribeOn(Schedulers.single()).subscribe(out -> log.info("3: subscribe," + out));
        Thread.sleep(1111);
    }

    @Data
    static class CallbackHandlerInner {
        private MonoSink monoSink;
        private FutureCallback responseCallback;

        public CallbackHandlerInner(MonoSink monoSink) {
            this.monoSink = monoSink;
            this.responseCallback = new FutureCallback() {
                @Override
                public void onSuccess(Object result) {
                    log.info("2: FutureCallback run," + Thread.currentThread().getName());
                    monoSink.success(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    monoSink.error(t);
                }
            };
        }
    }
}
