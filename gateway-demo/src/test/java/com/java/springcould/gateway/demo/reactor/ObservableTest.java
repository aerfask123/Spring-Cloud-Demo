package com.java.springcould.gateway.demo.reactor;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;


public class ObservableTest {

    public static void main(String[] args) {
        Observable observable = new Observable() {
            @Override
            public void notifyObservers(Object arg) {
                System.out.println("Observable [" + this + "] notifyObservers " + arg);
                super.setChanged();
                super.notifyObservers(arg);
            }
        };

        Observer first = (o, arg) -> System.out.println("Observable [" + o + "] update " + arg);

        observable.addObserver(first);
        observable.notifyObservers("100");
        observable.deleteObserver(first);

        Mono observableMono = Mono.create(new Consumer<MonoSink<Object>>() {
            @Override
            public void accept(MonoSink<Object> sink) {
                Observer second = new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        sink.success(arg);
                    }
                };
                observable.addObserver(second);
                observable.notifyObservers("200");
                sink.onDispose(new Disposable() {
                    @Override
                    public void dispose() {
                        observable.deleteObserver(second);
                        System.out.println("Observable delete observer " + second);
                    }
                });
            }
        });
        observableMono.subscribe(System.out::println);
    }
}
