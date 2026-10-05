package com.java.order.demo.util;

import com.java.order.demo.constants.ZooKeeperConstants;
import com.java.order.demo.factory.ClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

public class IDMaker {

    private CuratorFramework client;

    public void init() {
        this.client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);
        client.start();
    }

    private String createSeqNode(String pathPrefix) {
        try {
            String destPath = client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(pathPrefix);
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String makeId(String nodeName) {
        String str = createSeqNode(nodeName);
        if (null == str) {
            return null;
        }
        int index = str.lastIndexOf(nodeName);
        if (index >= 0) {
            index += nodeName.length();
            return index <= str.length() ? str.substring(index) : "";
        }

        return str;
    }

    public void destroy() {
        client.close();
    }
}
