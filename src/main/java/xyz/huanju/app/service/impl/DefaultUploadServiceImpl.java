package xyz.huanju.app.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.huanju.app.domain.UploadBean;
import xyz.huanju.app.exception.UnWritableException;
import xyz.huanju.app.exception.UploadException;
import xyz.huanju.app.service.UploadService;
import xyz.huanju.app.utils.FileUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author HuanJu
 * @date 2020/5/28 17:54
 */
//@Service
public class DefaultUploadServiceImpl implements UploadService {
    /**
     * 上传到临时文件夹
     */
    @Override
    public void upload(UploadBean uploadBean) {
        String cachePath = FileUtils.getCacheUrl(uploadBean.getPath(),uploadBean.getName());
        if (!new File(uploadBean.getPath()).canWrite()) {
            throw new UnWritableException(400, "文件夹不可写");
        }

        FileUtils.createDirIfNotExist(cachePath);

        MultipartFile file = uploadBean.getFile();

        if (!file.isEmpty()) {
            try {
                file.transferTo(Paths.get(cachePath + "/" + uploadBean.getSequence()));
            } catch (IOException e) {
                throw new UploadException(500, "上传失败，服务器错误");
            }
        }

        if (Objects.equals(uploadBean.getSequence(), uploadBean.getTotal())) {
            mergeFile(cachePath, uploadBean.getPath(), uploadBean.getName(), uploadBean.getTotal());
        }
    }

    @Override
    public void merge(String path, String name, Integer total) {

    }


    final static int BUFFER_SIZE = 16384;

    /**
     * 合并临时文件夹文件
     */
    private void mergeFile(String cachePath, String path, String name, Integer total) {
        File file = FileUtils.createFile(path, name);
        File temp;
        FileOutputStream fos = null;
        FileInputStream fis=null;
        byte[] buff=new byte[BUFFER_SIZE];
        try {
            fos=new FileOutputStream(file);
            for (int i = 1; i <= total; i++) {
                temp=new File(cachePath+"/"+i);
                fis=new FileInputStream(temp);
                int val;
                while ((val=fis.read(buff))!=-1){
                    fos.write(buff,0,val);
                }
                fos.flush();
                fis.close();
                /*
                移除该片段
                 */
                boolean r=temp.delete();
            }
            /*
            移除缓存目录
             */
            File cacheFile=new File(cachePath);
            boolean r=cacheFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
