package vip.tuoyang.zhonghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vip.tuoyang.zhonghe.config.webinterceptor.SecretInterceptor;

/**
 * @author AlanSun
 * @date 2021/9/16 8:44
 */
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SecretInterceptor secretInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(secretInterceptor).addPathPatterns("/*");
    }
}
