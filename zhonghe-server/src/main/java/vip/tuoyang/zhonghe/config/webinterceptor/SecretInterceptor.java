package vip.tuoyang.zhonghe.config.webinterceptor;

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
 * @date 2021/9/16 8:41
 */
@Component
public class SecretInterceptor implements HandlerInterceptor {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        final String secret = request.getHeader("secret");
        AssertUtils.notBlank(secret, "别乱调接口");
        AssertUtils.isTrue(secret.equals(serviceSystemProperties.getSecret()), "别乱调接口");
        return true;
    }
}
