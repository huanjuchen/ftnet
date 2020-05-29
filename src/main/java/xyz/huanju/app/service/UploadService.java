package xyz.huanju.app.service;

import xyz.huanju.app.domain.UploadBean;

/**
 * @author HuanJu
 */
public interface UploadService {

    void upload(UploadBean uploadBean);

    void merge(String path, String name, Integer total);
}
