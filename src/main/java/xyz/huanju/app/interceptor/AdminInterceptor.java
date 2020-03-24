package xyz.huanju.app.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HuanJu
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final List<String> localHosts = new ArrayList<>();

    static {
        localHosts.add("127.0.0.1");
        localHosts.add("0:0:0:0:0:0:0:1");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String rh = request.getRemoteAddr();
        List<String> hosts = localHosts;
        for (int i = 0; i < hosts.size(); i++) {
            String host = hosts.get(i);
            if (host.equals(rh)){
                return true;
            }
        }
        request.setAttribute("msg","只允许主机访问");
        request.getRequestDispatcher("error.html").forward(request,response);
        return false;
    }
}
