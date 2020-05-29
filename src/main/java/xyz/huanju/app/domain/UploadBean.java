package xyz.huanju.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HuanJu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadBean {

    private String name;

    private String path;

    private String md5;

    private Integer sequence;

    private Integer total;

    private MultipartFile file;

}
