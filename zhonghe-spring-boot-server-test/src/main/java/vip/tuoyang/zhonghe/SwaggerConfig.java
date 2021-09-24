package vip.tuoyang.zhonghe;


import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author AlanSun
 * @date 2021/5/28 11:31
 **/
@EnableOpenApi
@Configuration
public class SwaggerConfig {
    @Autowired(required = false)
    private OpenApiExtensionResolver openApiExtensionResolver;

    @Bean
    public Docket defaultApi2() {
        final Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(this.apiInfo())
                .groupName("1.0.0")
                .select()
                .apis(RequestHandlerSelectors.basePackage("vip.tuoyang"))
                .paths(PathSelectors.any())
                .build();

        if (openApiExtensionResolver != null) {
            docket.extensions(openApiExtensionResolver.buildExtensions("1.0.0"));
        }
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("中河中间件测试")
                .description("中河中间件测试")
                .termsOfServiceUrl("http://localhost:8085")
                .version("1.0.0").build();
    }
}
