package xyz.huanju.app.exception;

/**
 * @author HuanJu
 */
public class BaseException extends RuntimeException {

    private Integer code;

    private String message;

    public BaseException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
//        return super.fillInStackTrace();
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
