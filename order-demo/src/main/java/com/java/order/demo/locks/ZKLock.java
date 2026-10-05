package com.java.order.demo.locks;

import com.java.order.demo.util.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ZKLock implements Lock {

    private static final String ZK_PATH = "/test/lock";

    private static final String LOCK_PREFIX = ZK_PATH + "/";

    private static final long WAIT_TIME = 1000;
    private final AtomicInteger lockCount = new AtomicInteger(0);
    CuratorFramework client = null;
    private String priorPath = null;
    private String lockedShortPath = null;
    private String lockedPath = null;
    private Thread thread = null;

    public ZKLock() {
        client = ZKClient.instance.getClient();
        if (!ZKClient.instance.isNodeExists(ZK_PATH)) {
            ZKClient.instance.createNode(ZK_PATH);
        }
    }

    @Override
    public boolean lock() {
        synchronized (this) {
            if (lockCount.get() == 0) {
                thread = Thread.currentThread();
                lockCount.incrementAndGet();
            } else {
                if (!Thread.currentThread().equals(thread)) {
                    return false;
                }
                lockCount.incrementAndGet();
                return true;
            }
        }

        try {
            boolean locked = false;
            locked = tryLock();
            if (locked) {
                return true;
            }
            while (!locked) {
                await();
                List<String> waiters = getWaiters();
                if (checkLocked(waiters)) {
                    locked = true;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            unlock();
        }
        return false;
    }

    private void await() throws Exception {
        if (null == priorPath) {
            throw new Exception("prior path is null");
        }
        final CountDownLatch latch = new CountDownLatch(1);

        Watcher w = event -> {
            log.info("Watcher event: {}", event);
            log.info("[WatchedEvent]节点删除！");
            latch.countDown();
        };

        client.getData().usingWatcher(w).forPath(priorPath);

        latch.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    private boolean tryLock() throws Exception {
        lockedPath = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(LOCK_PREFIX);
        if (null == lockedPath) {
            throw new Exception("lock path is null");
        }
        lockedShortPath = getShortPath(lockedPath);

        List<String> waiters = getWaiters();

        if (checkLocked(waiters)) {
            return true;
        }

        int index = Collections.binarySearch(waiters, lockedShortPath);

        if (index < 0) {
            throw new Exception("节点没有找到：" + lockedShortPath);
        }

        priorPath = ZK_PATH + "/" + waiters.get(index - 1);
        return false;
    }

    private boolean checkLocked(List<String> waiters) {

        Collections.sort(waiters);

        if (lockedShortPath.equals(waiters.get(0))) {
            log.info("成功地获取分布式锁，节点为{}", lockedShortPath);
            return true;
        }
        return false;
    }

    private List<String> getWaiters() throws Exception {
        return client.getChildren().forPath(ZK_PATH);
    }

    private String getShortPath(String lockedPath) {
        int index = lockedPath.lastIndexOf(LOCK_PREFIX);
        if (index >= 0) {
            index += LOCK_PREFIX.length();
            return index <= lockedPath.length() ? lockedPath.substring(index) : "";
        }

        return lockedPath;
    }

    @Override
    public boolean unlock() {
        if (!Thread.currentThread().equals(thread)) {
            return false;
        }

        int newLockCount = lockCount.decrementAndGet();
        if (newLockCount < 0) {
            throw new IllegalArgumentException("计数不对：" + lockedPath);
        }

        if (newLockCount != 0) {
            return true;
        }
        try {
            if (ZKClient.instance.isNodeExists(lockedPath)) {
                client.delete().forPath(lockedPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
