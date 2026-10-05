package com.java.springcould.gateway.demo;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class RxJavaObserverDemo {

    @Test
    public void rxJavaActionLambda() {
        Observable<String> observable = Observable.just("apple", "banana", "pear");
        log.info("第一次订阅");
        observable.subscribe(s -> log.info("订阅者1收到：{}", s));

        log.info("第二次订阅");
        observable.subscribe(s -> log.info("订阅者2收到：{}", s),
                e -> log.error("订阅者2异常：{}", e));

        log.info("第三次订阅");
        observable.subscribe(s -> log.info("订阅者3收到：{}", s),
                e -> log.error("订阅者3异常：{}", e),
                () -> log.info("订阅者3完成"));
    }
}
