package com.java.sentinel.demo.service;

public interface UserService<T> {

    String connect();

    void register(T user);

    T findByName(String name);
}
