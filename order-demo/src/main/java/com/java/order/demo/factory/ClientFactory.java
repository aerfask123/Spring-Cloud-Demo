package com.java.order.demo.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ClientFactory {

    public static CuratorFramework createSimple(String connectionString) {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
    }

    public static CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy,
                                                     int sessionTimeoutMs, int connectionTimeoutMs) {
        return CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .retryPolicy(retryPolicy)
                .build();
    }
}
