package xyz.huanju.app.exception;

/**
 * @author HuanJu
 * @date 2020/5/28 18:17
 */
public class UploadException extends BaseException {

    public UploadException(Integer code, String message) {
        super(code, message);
    }
}
