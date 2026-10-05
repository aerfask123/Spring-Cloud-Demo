package com.java.springcould.gateway.demo.listener;

import java.util.List;

public interface MyMessageListener<T> {

    void onMessage(List<T> messages);
}
