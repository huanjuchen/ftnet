package xyz.huanju.app.utils;

import xyz.huanju.app.domain.Path;
import xyz.huanju.app.exception.UploadException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * @author HuanJu
 */
public class FileUtils {

    private final static String FIREFOX = "Firefox";

    private final static String EXISTS_SUB = "(1)";

    private final static Base64.Encoder encoder = Base64.getEncoder();


    public static void getSubPath(File file, List<Path> subPathList, List<Path> subFileList) {
        if (file == null || !file.exists() || !file.canRead()) {
            return;
        }
        File[] subFiles = file.listFiles();
        if (subFiles == null || subFiles.length == 0) {
            return;
        }
        for (File subFile : subFiles) {
            if (subFile.canRead()) {
                if (subFile.isDirectory()) {
                    subPathList.add(new Path(subFile.getName(), subFile.getAbsolutePath().replace('\\', '/')));
                } else if (subFile.isFile()) {
                    subFileList.add(new Path(subFile.getName(), subFile.getAbsolutePath().replace('\\', '/')));
                }
            }
        }
    }


    public static String getFileName(String agent, String filename) {
        if (agent.contains(FIREFOX)) {
            String encodeStr = encoder.encodeToString(filename.getBytes(StandardCharsets.UTF_8));
            StringBuilder filenameBuilder = new StringBuilder("=?utf-8?B?");
            filenameBuilder.append(encodeStr);
            filenameBuilder.append("?=");
            filename = filenameBuilder.toString();
        } else {
            try {
                filename = URLEncoder.encode(filename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println();
            }
        }

        return filename;
    }


    public static void createDirIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean r = file.mkdirs();
        }
    }


    public static File createFile(String path, String name) {
        File file = new File(path + "/" + name);
        boolean result;

        if (!file.exists()) {
            try {
                result = file.createNewFile();
            } catch (IOException e) {
                throw new UploadException(400, "创建文件失败");
            }

            if (!result) {
                throw new UploadException(400, "创建文件失败");
            }
            return file;
        }
            /*
            文件已存在
             */
//        String sub = name.substring(name.lastIndexOf('.'));
        String sub = null;
        String newName = null;
        int index = name.lastIndexOf('.');
        if (index != -1) {
            sub = name.substring(index);
            newName = name.replace(sub, "");
            newName = newName + EXISTS_SUB + sub;
        } else {
            /*
            无后缀处理
             */
            newName = name + EXISTS_SUB;
        }


        return createFile(path, newName);
    }


    public static String getCacheUrl(String path, String name) {
        return path + "/" + name + "Cache";
    }


}
