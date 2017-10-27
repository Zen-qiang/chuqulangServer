package com.dinglian.server.chuqulang.swagger;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dinglian.server.chuqulang.utils.DateUtils;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = { "com.dinglian.server.chuqulang.controller" })
public class SwaggerConfig {

	@Bean
	public Docket createRestApi() {
		 return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("接口列表 v1.0.0").description("更新日期：" + DateUtils.format(new Date(), DateUtils.yMdHms))
				.termsOfServiceUrl("http://localhost:8080/chuqulang/swagger-ui.html").version("1.0.0").build();
	}

}
