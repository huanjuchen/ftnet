package xyz.huanju.app.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.huanju.app.domain.ApiResult;
import xyz.huanju.app.exception.BaseException;

/**
 * @author HuanJu
 */
@RestControllerAdvice
public class ExceptionHandlerController {
    
    @ExceptionHandler(BaseException.class)
    public ApiResult handlerException(BaseException e){
        return new ApiResult(e.getCode(),e.getMessage(),null);
    }
    
}
