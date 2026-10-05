package com.java.order.demo;

import com.java.order.demo.constants.ZooKeeperConstants;
import com.java.order.demo.factory.ClientFactory;
import com.java.order.demo.util.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.Watcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

@Slf4j
public class ZkWatcherDemo {

    private final String workerPath = "/test/listener/remoteNode";

    private final String subWorkerPath = "/test/listener/remoteNode/id-";

    private CuratorFramework client = null;

    @BeforeEach
    public void init() {
        client = ClientFactory.createSimple(ZooKeeperConstants.ZK_ADDRESS);
        client.start();
    }

    @AfterEach
    public void destroy() {
        if (client != null) {
            try {
                client.delete().forPath(workerPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client.close();
        }
    }

    @Test
    public void testWatcher() {
        CuratorFramework client = ZKClient.instance.getClient();

        boolean isExist = ZKClient.instance.isNodeExists(workerPath);
        if (!isExist) {
            ZKClient.instance.createNode(workerPath);
        }

        try {
            Watcher watcher = event -> {
                log.info("Watcher event: {}", event);
            };

            byte[] content = client.getData().usingWatcher(watcher).forPath(workerPath);

            log.info("Content: {}", new String(content));
            client.setData().forPath(workerPath, "第一次修改内容".getBytes());
            client.setData().forPath(workerPath, "第二次修改内容".getBytes());
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNodeCache() {
        boolean isExist = ZKClient.instance.isNodeExists(workerPath);
        if (!isExist) {
            ZKClient.instance.createNode(workerPath);
        }
        CuratorFramework client = ZKClient.instance.getClient();
        try {
            CuratorCache curatorCache =
                    CuratorCache.builder(client, workerPath)
                            .withOptions(CuratorCache.Options.SINGLE_NODE_CACHE)
                            .build();
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forCreates(node -> {
                        log.info("ZNode节点创建，path={},data={},stat={}", node.getPath(), new String(node.getData(), Charset.defaultCharset()), node.getStat());
                    })
                    .forChanges((oldNode, node) -> {
                        log.info("ZNode节点原状态，path={},data={},stat={}", oldNode.getPath(), new String(oldNode.getData(), Charset.defaultCharset()), oldNode.getStat());
                        log.info("ZNode节点新状态，path={},data={},stat={}", node.getPath(), new String(node.getData(), Charset.defaultCharset()), node.getStat());
                    }).forDeletes(node -> {
                        log.info("ZNode节点删除，path={},data={},stat={}", node.getPath(), new String(node.getData(), Charset.defaultCharset()), node.getStat());
                    }).forInitialized(() -> {
                        log.info("Cache initialized");
                    })
                    .build();
            curatorCache.listenable().addListener(listener);
            curatorCache.start();

            client.setData().forPath(workerPath, "第一次修改内容".getBytes());
            client.setData().forPath(workerPath, "第二次修改内容".getBytes());
            client.setData().forPath(workerPath, "第三次修改内容".getBytes());
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPathChildrenCache() {
        boolean isExist = ZKClient.instance.isNodeExists(workerPath);
        if (!isExist) {
            ZKClient.instance.createNode(workerPath);
        }

        CuratorFramework client = ZKClient.instance.getClient();
        try {
            CuratorCache curatorCache =
                    CuratorCache.builder(client, workerPath)
                            .withOptions(CuratorCache.Options.DO_NOT_CLEAR_ON_CLOSE)
                            .withExceptionHandler((exception) -> {
                                log.error("Exception:{}", exception);
                            })
                            .build();

            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forCreates(node -> {
                        log.info("ZNode节点创建，path={},data={},stat={}", node.getPath(), new String(node.getData(), Charset.defaultCharset()), node.getStat());
                    })
                    .forChanges((oldNode, node) -> {
                        log.info("ZNode节点原状态，path={},data={},stat={}", oldNode.getPath(), new String(oldNode.getData(), Charset.defaultCharset()), oldNode.getStat());
                        log.info("ZNode节点新状态，path={},data={},stat={}", node.getPath(), new String(node.getData(), Charset.defaultCharset()), node.getStat());
                    }).forDeletes(node -> {
                        log.info("ZNode节点删除，path={},data={},stat={}", node.getPath(), new String(node.getData(), Charset.defaultCharset()), node.getStat());
                    }).forInitialized(() -> {
                        log.info("Cache initialized");
                    })
                    .build();
            curatorCache.listenable().addListener(listener);
            curatorCache.start();

            Thread.sleep(1000);

            for (int i = 0; i < 3; i++) {
                ZKClient.instance.createNode(subWorkerPath + i);
            }

            Thread.sleep(1000);
            for (int i = 0; i < 3; i++) {
                ZKClient.instance.deleteNode(subWorkerPath + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
