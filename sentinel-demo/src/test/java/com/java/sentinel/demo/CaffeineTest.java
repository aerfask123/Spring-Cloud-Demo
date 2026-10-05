package com.java.sentinel.demo;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

@Slf4j
//@ContextConfiguration(classes = CaffeineConfig.class)
@ExtendWith(SpringExtension.class)
public class CaffeineTest {

    @Test
    public void test() {
        Cache<String, String> cache = Caffeine.newBuilder().maximumSize(1024)
                .initialCapacity(1).expireAfterAccess(10, TimeUnit.SECONDS)
                .expireAfterWrite(2, TimeUnit.HOURS)
                .refreshAfterWrite(1, TimeUnit.HOURS)
                .recordStats().removalListener((key, value, cause) -> log.info("key: {}, value: {}, cause: {}", key, value, cause))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(@NonNull String key) throws Exception {
                        System.out.println("load key: " + key);
                        return "value_" + key;
                    }

                    @Override
                    public @Nullable String reload(String key, String oldValue) throws Exception {
                        System.out.println("reload key: " + key);
                        return CacheLoader.super.reload(key, oldValue);
                    }
                });

        cache.put("name", "疯狂创客");
        cache.put("key", "研究社群");

        @Nullable String value = cache.getIfPresent("name");
        System.out.println("value: " + value);

        cache.invalidate("name");
        value = cache.getIfPresent("name");
        System.out.println("value: " + value);
    }
}
