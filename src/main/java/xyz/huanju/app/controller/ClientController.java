package xyz.huanju.app.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.huanju.app.domain.ApiResult;
import xyz.huanju.app.domain.Path;
import xyz.huanju.app.storage.SharePath;
import xyz.huanju.app.utils.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author HuanJu
 */
@Controller
public class ClientController {

    private SharePath sharePath;

    @Autowired
    public void setSharePath(SharePath sharePath) {
        this.sharePath = sharePath;
    }

    @RequestMapping({"/", "/index"})
    public String toIndex(ModelMap map) {
        map.addAttribute("pathList", sharePath.keys());
        return "index";
    }

    @GetMapping("/indexData")
    @ResponseBody
    public ApiResult indexData() {
        List<String> keys = sharePath.keys();
//        try {
//            TimeUnit.SECONDS.sleep(3L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return ApiResult.ok(keys);
    }


    /**
     * 返回文件夹的子目录以及文件
     * 以及父目录和名字
     *
     * @param queryPath 查询的路径
     * @return 格式如下
     * {
     * parentPath: "String",  父路径
     * name: "String", 当前文件夹名
     * subPaths: [Array, Array, Array], 子目录集合
     * subFiles: [Array, Array, Array]  子文件集合
     * }
     */
    @GetMapping("/path/json")
    @ResponseBody
    public ApiResult pathData(String queryPath) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Path> subPaths = new ArrayList<>();
        List<Path> subFiles = new ArrayList<>();
        File file = null;
        String parentName = null;
        String parentPath = null;
        String name = null;

        if (queryPath == null || queryPath.length() == 0) {
            return ApiResult.ok();
        }
        String path = sharePath.get(queryPath);
        if (path != null) {
            file = new File(path);
        } else {
            file = new File(queryPath);
        }
        if (!file.exists() || file.isFile() || !file.canRead()) {
            return new ApiResult(400, "文件夹不存在或不可读", null);
        }
        FileUtils.getSubPath(file, subPaths, subFiles);
        name = file.getName();
        File parentFile = file.getParentFile();
        if (parentFile != null && parentFile.exists() && parentFile.canRead()) {
            parentName = parentFile.getName();
            parentPath = parentFile.getAbsolutePath();
        }


        if (parentPath != null) {
            resultMap.put("parentPath", parentPath);
        }
        resultMap.put("subPaths", subPaths);
        resultMap.put("subFiles", subFiles);
        resultMap.put("name", name);
        resultMap.put("url",file.getAbsolutePath());
        return ApiResult.ok(resultMap);
    }


    @RequestMapping("/path")
    public String viewPath(String key, String abPath, ModelMap map) {
        List<Path> subPaths = new ArrayList<>();
        List<Path> subFiles = new ArrayList<>();
        File file = null;
        String parentName = null;
        String parentPath = null;
        String name = null;
        if (abPath != null && abPath.length() > 0) {
            file = new File(abPath);
        } else if (key != null && key.length() > 0) {
            String pathValue = sharePath.get(key);
            if (pathValue != null && pathValue.length() > 0) {
                file = new File(pathValue);
            }
        }

        if (file != null) {
            FileUtils.getSubPath(file, subPaths, subFiles);
            name = file.getName();
            File parentFile = file.getParentFile();
            if (parentFile != null && parentFile.exists() && parentFile.canRead()) {
                parentName = parentFile.getName();
                parentPath = parentFile.getAbsolutePath();
            }
        }

        if (parentPath != null) {
            map.addAttribute("parentPath", parentPath);
        }
        map.addAttribute("subPaths", subPaths);
        map.addAttribute("subFiles", subFiles);
        map.addAttribute("name", name);
        return "path";
    }

    @RequestMapping("/file")
    public String viewFile(String path, ModelMap map) {
        File file = new File(path);
        if (!file.exists()) {
            map.addAttribute("msg", "文件不存在");
            return "/error";
        }
        if (!file.canRead()) {
            map.addAttribute("msg", "文件不可读");
        }
        String name = file.getName();
        String url = file.getAbsolutePath();
        long size = file.length();
        map.addAttribute("name", name);
        map.addAttribute("url", url);
        map.addAttribute("size", size);
        return "file";
    }

    /**
     * @param fileUrl 文件路径
     * @return 返回格式
     * {
     * name: "文件",
     * url: "文件路径",
     * size: long,
     * type: 文件类型
     * }
     */

    @GetMapping("/file/detail")
    @ResponseBody
    public ApiResult viewFile(String fileUrl, HttpServletRequest request) {
        File file = new File(fileUrl);
        if (!file.exists() || !file.canRead()) {
            return new ApiResult(400, "文件不存在或不可访问", null);
        }
        String name = file.getName();
        String url = file.getAbsolutePath();
        long size = file.length();
        String type = request.getServletContext().getMimeType(name);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name",name);
        resultMap.put("url",url);
        resultMap.put("size",size);
        resultMap.put("type",type);
        return ApiResult.ok(resultMap);
    }

    final static int BUFFER_SIZE = 16384;

    @RequestMapping("/file/download")
    public String downloadFile(String path, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        File file = new File(path);
        if (!file.exists()) {
            map.addAttribute("msg", "文件不存在");
            return "error";
        }
        if (!file.isFile()) {
            map.addAttribute("msg", "不是一个文件");
            return "error";
        }
        if (!file.canRead()) {
            map.addAttribute("msg", "文件不可读");
            return "error";
        }
        RandomAccessFile inputStream = null;
        ServletOutputStream outputStream = null;
        String range = request.getHeader("Range");
        //MIME类型
        String mimeType = request.getServletContext().getMimeType(path);
        //文件长度
        long fileLen = file.length();
        //下载起始位置
        long startPos = 0;
        long endPos = file.length() - 1;
        //下载长度
        long downloadSize = fileLen;
        String filename = file.getName();
        //断点续传请求处理
        if (range != null && range.length() > 0) {
            try {
                String temp = range.replaceAll("bytes=", "");
                String[] arr = temp.split("-");
                startPos = Long.parseLong(arr[0]);
                if (arr.length == 2) {
                    endPos = Long.parseLong(arr[1]);
                }
                response.setStatus(206);
            } catch (Exception e) {
                System.out.println("----");
            }
        }
        downloadSize = endPos - startPos + 1;

        try {

            //缓冲区大小
            int bufferLen = (int) (downloadSize < BUFFER_SIZE ? downloadSize : BUFFER_SIZE);
            byte[] buffer = new byte[bufferLen];
            inputStream = new RandomAccessFile(file, "r");
            outputStream = response.getOutputStream();
            //响应头设置
            response.setContentType(mimeType);
            response.setHeader("Content-Range", "bytes " + startPos + "-" + endPos + "/" + fileLen);
            response.setHeader("Content-Disposition", "attachment; filename=" + FileUtils.getFileName(request.getHeader("User-Agent"), filename));
            response.setContentLengthLong(downloadSize);
            response.setHeader("Accept-Ranges", "bytes");
            //设置下载开始位置
            inputStream.seek(startPos);
            //当前写到客户端的大小
            long count = 0;
            int num = 0;
            while ((num = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, num);
                count = count + num;
                //不满足缓冲区大小处理
                if (downloadSize - count < bufferLen) {
                    bufferLen = (int) (downloadSize - count);
                    if (bufferLen == 0) {
                        break;
                    }
                    //新建缓冲区
                    buffer = new byte[bufferLen];
                }
            }
            outputStream.flush();
        } catch (IOException e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    @GetMapping("/file/down")
    public void downFile(String path,HttpServletRequest request, HttpServletResponse response){
        System.out.println(path);
        File file = new File(path);
        if (!file.exists()||!file.isFile()||!file.canRead()) {
            return;
        }
        RandomAccessFile inputStream = null;
        ServletOutputStream outputStream = null;
        String range = request.getHeader("Range");
        //MIME类型
        String mimeType = request.getServletContext().getMimeType(path);
        //文件长度
        long fileLen = file.length();
        //下载起始位置
        long startPos = 0;
        long endPos = file.length() - 1;
        //下载长度
        long downloadSize = fileLen;
        String filename = file.getName();
        //断点续传请求处理
        if (range != null && range.length() > 0) {
            try {
                String temp = range.replaceAll("bytes=", "");
                String[] arr = temp.split("-");
                startPos = Long.parseLong(arr[0]);
                if (arr.length == 2) {
                    endPos = Long.parseLong(arr[1]);
                }
                response.setStatus(206);
            } catch (Exception e) {
                System.out.println("----");
            }
        }
        downloadSize = endPos - startPos + 1;

        try {

            //缓冲区大小
            int bufferLen = (int) (downloadSize < BUFFER_SIZE ? downloadSize : BUFFER_SIZE);
            byte[] buffer = new byte[bufferLen];
            inputStream = new RandomAccessFile(file, "r");
            outputStream = response.getOutputStream();
            //响应头设置
            response.setContentType(mimeType);
            response.setHeader("Content-Range", "bytes " + startPos + "-" + endPos + "/" + fileLen);
            response.setHeader("Content-Disposition", "attachment; filename=" + FileUtils.getFileName(request.getHeader("User-Agent"), filename));
            response.setContentLengthLong(downloadSize);
            response.setHeader("Accept-Ranges", "bytes");
            //设置下载开始位置
            inputStream.seek(startPos);
            //当前写到客户端的大小
            long count = 0;
            int num = 0;
            while ((num = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, num);
                count = count + num;
                //不满足缓冲区大小处理
                if (downloadSize - count < bufferLen) {
                    bufferLen = (int) (downloadSize - count);
                    if (bufferLen == 0) {
                        break;
                    }
                    //新建缓冲区
                    buffer = new byte[bufferLen];
                }
            }
            outputStream.flush();
        } catch (IOException e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }



}
