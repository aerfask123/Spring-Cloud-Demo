package com.java.springcould.gateway.demo;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class ObservableDemo {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Throwable {
                for (int i = 0; i < 10; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onComplete();
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                log.info("订阅:{}", d);
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                log.info("接收Observerable中发射的值：{}", integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                log.error("异常：{}", e);
            }

            @Override
            public void onComplete() {
                log.info("完成");
            }
        });
    }
}
