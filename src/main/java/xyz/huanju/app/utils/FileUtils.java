package xyz.huanju.app.utils;

import xyz.huanju.app.domain.Path;

import java.io.File;
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

    private final static Base64.Encoder encoder=Base64.getEncoder();


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
//            BASE64Encoder base64Encoder = new BASE64Encoder();

//            filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes(StandardCharsets.UTF_8))+"?=";

            String encodeStr=encoder.encodeToString(filename.getBytes(StandardCharsets.UTF_8));
            StringBuilder filenameBuilder=new StringBuilder("=?utf-8?B?");
            filenameBuilder.append(encodeStr);
            filenameBuilder.append("?=");
            filename=filenameBuilder.toString();

        } else {
            try {
                filename = URLEncoder.encode(filename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println();
            }
        }

        return filename;
    }

}
