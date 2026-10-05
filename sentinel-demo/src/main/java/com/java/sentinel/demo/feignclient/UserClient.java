package com.java.sentinel.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sec-demo-service", url = "${remote.service.url}")
public interface UserClient {

    @GetMapping("/test/hello")
    String connect();
}
