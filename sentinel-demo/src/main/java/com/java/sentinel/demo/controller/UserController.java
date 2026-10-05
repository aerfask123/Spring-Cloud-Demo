package com.java.sentinel.demo.controller;

import com.java.sentinel.demo.service.ImdbService;
import com.java.sentinel.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aerfa
 */
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ApplicationContext applicationContext;

    private final ImdbService imdbService;

    @RequestMapping("connect")
    public String connect() {
        return userService.connect();
    }

    @RequestMapping("shutdown")
    public String shutdown() {
        SpringApplication.exit(applicationContext);
        return "shutdown";
    }

    @RequestMapping("download")
    public String download() {
        imdbService.download();
        return "download";
    }

    @RequestMapping("download/image")
    public String downloadImage() {
        imdbService.downloadImage();
        return "downloadImage";
    }
}
