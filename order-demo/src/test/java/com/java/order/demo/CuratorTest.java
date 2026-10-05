package com.java.order.demo;

import com.java.order.demo.constants.ZooKeeperConstants;
import com.java.order.demo.factory.ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class CuratorTest {

    @Test
    public void createNode() {
        CuratorFramework client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);

        try {
            client.start();
            String data = "hello";
            byte[] payload = data.getBytes(StandardCharsets.UTF_8);
            String zkPath = "/test/CRUD/node-1";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath, payload);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void createEphNode() {
        CuratorFramework client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);

        try {
            client.start();
            String data = "Ephemeral";
            byte[] payload = data.getBytes(StandardCharsets.UTF_8);
            String zkPath = "/eph/node1";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(zkPath, payload);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void readNode() {
        CuratorFramework client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);

        try {
            client.start();
            String zkPath = "/test/CRUD/node-1";
            Stat stat = client.checkExists().forPath(zkPath);
            if (null != stat) {
                byte[] payload = client.getData().forPath(zkPath);
                String data = new String(payload, StandardCharsets.UTF_8);
                System.out.println(data);
                String parentPath = "/test";
                List<String> children = client.getChildren().forPath(parentPath);
                for (String child : children) {
                    log.info("child:{}", child);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void updateNode() {
        CuratorFramework client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);
        try {
            client.start();
            String data = "hello world";
            byte[] payload = data.getBytes(StandardCharsets.UTF_8);
            String zkPath = "/test/CRUD/node-1";
            client.setData().forPath(zkPath, payload);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void updateNodeAsync() {
        CuratorFramework client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);
        try {
            AsyncCallback.StringCallback callback = (client1, path, ctx, stat) -> {
                log.info("update node client1:{}, path:{}, ctx:{}, stat:{}", client1, path, ctx, stat);
            };
            client.start();
            String data = "hello,every body";
            byte[] payload = data.getBytes(StandardCharsets.UTF_8);
            String zkPath = "/test/CRUD/remotenode-1";
            // client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkPath, payload);
            client.setData().inBackground(callback).forPath(zkPath, payload);
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void deleteNode() {
        CuratorFramework client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);
        try {
            client.start();
            String zkPath = "/test/CRUD/remotenode-1";
            client.delete().forPath(zkPath);
            String parentPath = "/test";
            List<String> children = client.getChildren().forPath(parentPath);
            for (String child : children) {
                log.info("child:{}", child);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
