package xyz.huanju.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.huanju.app.domain.ApiResult;
import xyz.huanju.app.domain.UploadBean;
import xyz.huanju.app.service.UploadService;

import javax.annotation.Resource;

/**
 * @author HuanJu
 */
@RestController
public class UploadController {


    private UploadService uploadService;

    @Resource
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/file/upload")
    public ApiResult upload(UploadBean uploadBean){
        uploadService.upload(uploadBean);
        return ApiResult.ok();
    }

    @PostMapping("/file/merge")
    public ApiResult merge(String path,String name,Integer total){
        uploadService.merge(path, name, total);
        return ApiResult.ok();
    }


}
