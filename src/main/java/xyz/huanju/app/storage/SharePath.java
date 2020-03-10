package xyz.huanju.app.storage;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HuanJu
 * @date 2020/3/7 11:09
 */
@Component
public class SharePath {

    /**
     * 路径容器
     */
    private volatile Map<String, String> shareMap;

    /**
     * 最大长度
     */
    private final int maxSize = 10;

    private final ReentrantLock lock = new ReentrantLock();

    public SharePath() {
        shareMap = new HashMap<>();
    }

    /**
     * 添加
     */
    public String add(String path) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (isFull()) {
                return null;
            }
            String key = getKey(path);
            if (key == null) {
                return null;
            }
            Map<String, String> elements = getShareMap();
            elements.put(key, path);
            setShareMap(elements);
            return key;
        } finally {
            lock.unlock();
        }
    }

    public String remove(String key) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }
            String path = null;
            Map<String, String> elements = getShareMap();
            path = elements.remove(key);
            setShareMap(elements);
            return path;
        } finally {
            lock.unlock();
        }
    }

    public String get(String key) {
        if (isEmpty()) {
            return null;
        }
        return getShareMap().get(key);

    }

    public List<String> keys() {
        Map<String, String> map = getShareMap();
        Set<String> stringSet = map.keySet();
        return new ArrayList<>(stringSet);

    }

    private boolean isFull() {
        int size = shareMap.size();
        return size == maxSize;
    }

    private boolean isEmpty() {
        int size = shareMap.size();
        return size == 0;
    }


    private String getKey2(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        String str = path;
        int lastIndex = str.lastIndexOf('/');
        int len = str.length();

        if (lastIndex == len-1) {
            str = str.substring(0, path.length() - 1);
        }
        lastIndex = str.lastIndexOf('/');
        if (lastIndex == -1) {
            return str;
        }
        return str.substring(lastIndex + 1);
    }

    private String getKey(String path) {
        File file=new File(path);
        if (!file.exists()){
            return null;
        }
        return file.getName();
    }



    private Map<String, String> getShareMap() {
        return shareMap;
    }

    private void setShareMap(Map<String, String> shareMap) {
        this.shareMap = shareMap;
    }
}
