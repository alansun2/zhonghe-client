package vip.tuoyang.zhonghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author AlanSun
 * @date 2021/9/28 0:47
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        final String secret = request.getHeader("secret");
        AssertUtils.notBlank(secret, "没有权限");
        AssertUtils.isTrue(serviceSystemProperties.getSecret().equals(secret), "没有权限");
        return true;
    }
}
