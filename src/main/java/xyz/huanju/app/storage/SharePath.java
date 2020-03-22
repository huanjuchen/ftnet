package xyz.huanju.app.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
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
     * 文件名
     */
    private final String initFileName = "ftnet";

    private final String charset="utf-8";


    /**
     * 最大长度
     */
    private final int maxSize = 10;

    private final ReentrantLock lock = new ReentrantLock();

    public SharePath() {
        shareMap = new HashMap<>();
        init();
    }

    private void init() {
        String url = getInitFileUrl();
        File file=new File(url);
        if (!file.exists()||file.isDirectory()){
            return;
        }
        List<String> initPath=getInitPath(url);
        for (String path : initPath) {
            File checkFile = new File(path);
            if (checkFile.exists() && checkFile.isDirectory()) {
                this.add(path);
            }
        }
    }




    private String getInitFileUrl() {
        String path=System.getProperty("user.dir");
        return path+"/"+initFileName;
    }

    private List<String> getInitPath(String url){
        InputStream in=null;
        InputStreamReader isr=null;
        BufferedReader br = null;
        List<String> list=new ArrayList<>();
        try {
            in=new FileInputStream(url);
            isr=new InputStreamReader(in, Charset.forName(charset));
            br=new BufferedReader(isr);
            String str=null;
            while ((str=br.readLine())!=null){
                if (list.size()>=10){
                    break;
                }
                list.add(str);
            }
        } catch (IOException e) {
            System.out.println();
        } finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (isr!=null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //
            if (br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
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


    private String getKey(String path) {
        File file = new File(path);
        if (!file.exists()) {
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
