package xyz.huanju.app.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.huanju.app.domain.UploadBean;
import xyz.huanju.app.exception.UnWritableException;
import xyz.huanju.app.exception.UploadException;
import xyz.huanju.app.service.UploadService;
import xyz.huanju.app.utils.FileUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

/**
 * @author HuanJu
 * <p>
 * 处理上传文件与合并文件分开
 */
@Service
public class UploadMergeUploadServiceImpl implements UploadService {
    @Override
    public void upload(UploadBean uploadBean) {
        String cachePath = FileUtils.getCacheUrl(uploadBean.getPath(), uploadBean.getName());
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
    }


    final static int BUFFER_SIZE = 16384;

    /**
     * @param path  上传路径
     * @param name  文件名
     * @param total 片段数量
     */
    @Override
    public void merge(String path, String name, Integer total) {
        //分散片段文件夹
        String cachePath = FileUtils.getCacheUrl(path, name);
        //输出文件
        File outFile = FileUtils.createFile(path, name);
        //片段文件
        File fragmentFile = null;
        /*
        片段输入流及通道
         */
        FileOutputStream fos = null;
        FileChannel oc = null;
        /*
        输出通道
         */
        FileInputStream fis = null;
        FileChannel ic = null;

        //缓冲区
        ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);

        try {
            fos = new FileOutputStream(outFile);
            oc = fos.getChannel();
            for (int i = 1; i <= total; i++) {
                fragmentFile=new File(cachePath+"/"+i);
                /*
                合并
                 */
                fis=new FileInputStream(fragmentFile);
                ic=fis.getChannel();
                while (ic.read(buff)!=-1){
                    buff.flip();
                    oc.write(buff);
                    buff.clear();
                }

                ic.close();
                fis.close();
                /*
                删除片段
                 */
                boolean b = fragmentFile.delete();
            }

            /*
            移除缓存目录
             */
            File cacheFile=new File(cachePath);
            boolean r=cacheFile.delete();


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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
            if (oc!=null){
                try {
                    oc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ic!=null){
                try {
                    ic.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
