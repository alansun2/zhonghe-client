package vip.tuoyang.zhonghe;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author AlanSun
 * @date 2021/9/3 14:59
 */
@SpringBootApplication(scanBasePackages = "vip.tuoyang")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
