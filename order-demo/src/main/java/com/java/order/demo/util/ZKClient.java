package com.java.order.demo.util;

import com.java.order.demo.constants.ZooKeeperConstants;
import com.java.order.demo.factory.ClientFactory;
import org.apache.curator.framework.CuratorFramework;

public class ZKClient {

    public static ZKClient instance = new ZKClient();

    public static CuratorFramework client;

    static {
        client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);
        client.start();
    }

    public CuratorFramework getClient() {
        return client;
    }

    public boolean isNodeExists(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createNode(String path) {
        try {
            if (!isNodeExists(path)) {
                client.create().creatingParentsIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNode(String path) {
        try {
            if (isNodeExists(path)) {
                client.delete().deletingChildrenIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
