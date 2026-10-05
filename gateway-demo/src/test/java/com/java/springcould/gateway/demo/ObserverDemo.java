package com.java.springcould.gateway.demo;

import java.util.Observable;

public class ObserverDemo extends Observable {

    public static void main(String[] args) {
        ObserverDemo demo = new ObserverDemo();

        demo.addObserver((o, arg) -> System.out.println("ObserverDemo update A"));

        demo.addObserver((o, arg) -> System.out.println("ObserverDemo update B"));

        demo.setChanged();
        demo.notifyObservers();
    }
}
