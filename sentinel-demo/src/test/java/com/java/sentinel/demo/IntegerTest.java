package com.java.sentinel.demo;

public class IntegerTest {

    public static void main(String[] args) {
        Integer a = 100;
        Integer b = 100;
        Integer c = Integer.valueOf(100);
        Integer d = Integer.valueOf(100);
        System.out.println(a == b);
        System.out.println(a == c);
    }
}
