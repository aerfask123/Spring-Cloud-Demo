package com.java.sentinel.demo.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController("hello")
public class HelloController {

    @RequestMapping("hello")
    @SentinelResource(value = "HelloWorld", blockHandler = "blockHandler", fallback = "fallback")
    public String hello(@RequestParam String name) throws InterruptedException, FlowException {
        System.out.println("hello " + name);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(10) < 5) {
            throw new RuntimeException("Business error");
        } else {
            throw new FlowException("Blocked bySentinel: ");
        }
    }

    public String blockHandler(String name, BlockException exception) {
        return "name=" + name + ", Blocked bySentinel: " + exception.getClass().getSimpleName();
    }

    public String fallback(String name, Throwable t) {
        return "name=" + name + ", Fallback for business error: " + t.getMessage();
    }
}
