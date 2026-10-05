package com.java.order.demo.util;

import com.java.order.demo.constants.ZooKeeperConstants;
import com.java.order.demo.factory.ClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

public class SnowFlakeIdWorker {

    public static SnowFlakeIdWorker instance = new SnowFlakeIdWorker();
    private final String pathPrefix = "/test/IDMaker/worker-";
    private Long nodeId = null;
    private String pathRegistered = null;
    private transient CuratorFramework zkClient = null;

    private SnowFlakeIdWorker() {
        this.zkClient = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);

        init();
    }

    private void init() {
        zkClient.start();

        try {
            byte[] payload = pathPrefix.getBytes();

            pathRegistered = zkClient.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(pathPrefix, payload);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getNodeId() {
        if (null != nodeId)
            return nodeId;

        if (null == pathRegistered) {
            throw new RuntimeException("pathRegistered is null");
        }

        int index = pathRegistered.lastIndexOf(pathPrefix);
        String sid = null;
        if (index >= 0) {
            index += pathPrefix.length();
            sid = index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
        }
        if (sid == null) {
            throw new RuntimeException("sid is null");
        }

        nodeId = Long.parseLong(sid);
        return nodeId;
    }
}
