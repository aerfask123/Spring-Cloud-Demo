package com.java.springcould.gateway.demo;

public class HappensBeforeExample {
    private volatile boolean flag = false;
    private int value = 0;

    public static void main(String[] args) {
        HappensBeforeExample example = new HappensBeforeExample();
        example.reader();
        example.writer();
        example.reader();
    }

    public void writer() {
        value = 42;     // 普通写操作
        flag = true;    // volatile 写操作
    }

    public void reader() {
        if (flag) {     // volatile 读操作
            System.out.println(value); // 保证输出 42
        }
    }
}
