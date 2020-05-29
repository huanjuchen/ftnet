package xyz.huanju.app.exception;

/**
 * @author HuanJu
 * @date 2020/5/23 22:02
 */
public class UnReadableException extends BaseException {
    public UnReadableException(Integer code, String message) {
        super(code, message);
    }
}
