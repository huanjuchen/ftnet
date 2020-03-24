package xyz.huanju.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.huanju.app.interceptor.AdminInterceptor;

/**
 * @author HuanJu
 * @date 2020/3/24 15:48
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {


    private AdminInterceptor adminInterceptor;

    @Autowired
    public void setAdminInterceptor(AdminInterceptor adminInterceptor) {
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
    }
}
