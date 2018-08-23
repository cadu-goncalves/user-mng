package com.creativedrive.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Boostrap Swagger integration
 *
 * @see https://springfox.github.io/springfox/docs/current/
 */
@Configuration
@EnableSwagger2
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerConfig {

    /**
     * Factory for doclet used to generate API documentation.
     *
     * @return {@link springfox.documentation.spring.web.plugins.Docket}
     */
    @Bean
    public Docket userApi() {
        // Minimal configuration
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.creativedrive.user"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("User Management Service API")
                .contact(new Contact("Carlos Eduardo", "", "cadu.goncalves@gmail.com"))
                .description("API for user management")
                .version("1.0")
                .build();
    }

}
