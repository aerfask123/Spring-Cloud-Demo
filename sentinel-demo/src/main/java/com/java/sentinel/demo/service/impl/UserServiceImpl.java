package com.java.sentinel.demo.service.impl;

import com.java.sentinel.demo.domain.po.User;
import com.java.sentinel.demo.feignclient.UserClient;
import com.java.sentinel.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService<User> {

    private final UserClient userClient;

    @Override
    public String connect() {
        return userClient.connect();
    }

    @Override
    public void register(User user) {

    }

    @Override
    public User findByName(String name) {
        return null;
    }
}
