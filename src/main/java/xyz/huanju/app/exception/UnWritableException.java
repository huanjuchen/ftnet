package xyz.huanju.app.exception;

/**
 * @author HuanJu
 * @date 2020/5/28 18:13
 */
public class UnWritableException extends BaseException {
    public UnWritableException(Integer code, String message) {
        super(code, message);
    }
}
