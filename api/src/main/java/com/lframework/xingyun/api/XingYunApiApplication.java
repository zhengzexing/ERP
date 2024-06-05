package com.lframework.xingyun.api;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.lframework.starter.web.annotations.locker.EnableLock;
import com.lframework.starter.web.annotations.locker.LockType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableLock(type = LockType.REDIS)
@ServletComponentScan(basePackages = {"com.lframework.xingyun"})
@SpringBootApplication(scanBasePackages = {"com.lframework.xingyun"})
@MapperScan({"com.lframework.xingyun.**.mappers"})
public class XingYunApiApplication {

  public static void main(String[] args) {

    SpringApplication.run(XingYunApiApplication.class, args);
  }

  /**
   * Swagger 自定义配置信息
   */
  @Configuration
  public static class SwaggerApiConfiguration {

    @Bean(value = "defaultApi")
    public Docket defaultApi(OpenApiExtensionResolver openApiExtensionResolver) {

      Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).groupName("决策分析ERP")
          .select()
          .apis(RequestHandlerSelectors.basePackage("com.lframework.xingyun.api")
              .or(RequestHandlerSelectors.basePackage("com.lframework.xingyun.basedata"))
              .or(RequestHandlerSelectors.basePackage("com.lframework.xingyun.chart"))
              .or(RequestHandlerSelectors.basePackage("com.lframework.xingyun.core"))
              .or(RequestHandlerSelectors.basePackage("com.lframework.xingyun.sc"))
              .or(RequestHandlerSelectors.basePackage("com.lframework.xingyun.settle")))
          .paths(PathSelectors.any())
          .build().extensions(openApiExtensionResolver.buildSettingExtensions());
      return docket;
    }

    @Bean
    public ApiInfo apiInfo() {

      return new ApiInfoBuilder().title("决策分析ERP接口文档").description("# 决策分析ERP接口文档")
          .contact("lframework@163.com")
          .build();
    }
  }
}
