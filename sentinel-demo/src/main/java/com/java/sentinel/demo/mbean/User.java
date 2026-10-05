package com.java.sentinel.demo.mbean;

public class User implements UserMBean {

    @Override
    public String getName() {
        return "张飞";
    }

    @Override
    public String getPassword() {
        return "密码不可见";
    }

    @Override
    public String getPhone() {
        return "15621354564";
    }

    @Override
    public void say() {
        System.out.println("hello JMX");
    }
}
