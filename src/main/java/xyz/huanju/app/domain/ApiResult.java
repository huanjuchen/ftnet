package xyz.huanju.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.system.ApplicationPid;

/**
 * @author HuanJu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ApiResult {

    private Integer code;

    private String msg;

    private Object data;

    public static ApiResult ok() {
        return new ApiResult(200, "OK", null);
    }

    public static ApiResult ok(Object data) {
        return new ApiResult(200, "OK", data);
    }

}
