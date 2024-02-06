package com.jfecm.bankaccountmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jfecm.bankaccountmanagement"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfoMetaData());
    }

    private ApiInfo apiInfoMetaData() {

        return new ApiInfoBuilder().title("API Documentation")
                .description("Project designed to offer Customer, Accounts and Transactions Accounts account services in the context of a banking entity.")
                .contact(new Contact("JFECM", "https://github.com/joaquincorimayo", "jfecm.dev@gmail.com"))
                .license("MIT License")
                .licenseUrl("https://github.com/jfecm/bank-account-management/blob/master/LICENSE.md")
                .version("0.0.1-SNAPSHOT")
                .build();
    }
}
