package com.beanSpot.WEB3_4_Poten_BE.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "bearerAuth";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(apiInfo())
			.addSecurityItem(new SecurityRequirement()
				.addList(SECURITY_SCHEME_NAME))
			.components(new Components()
				.addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme()));
	}

	private Info apiInfo() {
		return new Info()
			.title("BeanSpot API")
			.description("BeanSpot 서비스의 REST API 문서입니다")
			.version("1.0.0")
			.contact(new Contact()
				.name("BeanSpot Team")
				.email("contact@beanspot.com")
				.url("https://www.beanspot.com"))
			.license(new License()
				.name("Apache License Version 2.0")
				.url("http://www.apache.org/licenses/LICENSE-2.0"));
	}

	private SecurityScheme createSecurityScheme() {
		return new SecurityScheme()
			.name(SECURITY_SCHEME_NAME)
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");
	}
}