package vip.tuoyang.zhonghe.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import vip.tuoyang.base.util.SpringHelper;

/**
 * @author AlanSun
 * @date 2021/9/3 14:22
 */
@Import(SpringHelper.class)
@ComponentScan(basePackages = "vip.tuoyang.zhonghe")
@Configuration(proxyBeanMethods = true)
public class ZhongHeAutoConfiguration {
}
