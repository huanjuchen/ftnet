package xyz.huanju.app.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.huanju.app.domain.UploadBean;
import xyz.huanju.app.exception.UnWritableException;
import xyz.huanju.app.exception.UploadException;
import xyz.huanju.app.service.UploadService;
import xyz.huanju.app.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author HuanJu
 * @date 2020/5/30 16:30
 */
//@Service
public class MappedUploadServiceImpl implements UploadService {

    private final static Logger logger = LoggerFactory.getLogger(MappedUploadServiceImpl.class);


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

    @Override
    public void merge(String path, String name, Integer total) {

        long start = System.currentTimeMillis();
        //分散片段文件夹
        String cachePath = FileUtils.getCacheUrl(path, name);

        String fraUrl;
        int count = 0;

        /*
        输入通道和输出通道
         */
        FileChannel outChannel = null;
        FileChannel inChannel = null;

        MappedByteBuffer inMapBuf;
        MappedByteBuffer outMapBuf;
        try {
            outChannel = FileChannel.open(Paths.get(path + "/" + name), StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE, StandardOpenOption.READ);
            for (int i = 1; i <= total; i++) {
                fraUrl = cachePath + "/" + i;
                inChannel = FileChannel.open(Paths.get(fraUrl), StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE);
                outMapBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, count, inChannel.size());
                inMapBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
                outMapBuf.put(inMapBuf);
                count += inMapBuf.limit();
//                outChannel.write(inMapBuf);
                inChannel.close();
            }
            boolean b = new File(cachePath).delete();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long end = System.currentTimeMillis();

        logger.debug("合并用时" + (end - start) + "ms");


    }
}
