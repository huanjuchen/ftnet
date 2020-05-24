package xyz.huanju.app.exception;

/**
 * @author HuanJu
 */
public class NotFoundException extends BaseException {
    public NotFoundException(Integer code, String message) {
        super(code, message);
    }
}
