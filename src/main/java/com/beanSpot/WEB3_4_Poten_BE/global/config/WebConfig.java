package com.beanSpot.WEB3_4_Poten_BE.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")  // 모든 경로에 대해 CORS 설정 적용
			.allowedOriginPatterns("*")  // 모든 출처 허용 (개발 환경용)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // 허용할 HTTP 메서드
			.allowedHeaders("Authorization", "Content-Type", "X-Requested-With",
				"Accept", "Origin", "Access-Control-Request-Method",
				"Access-Control-Request-Headers", "*")  // 허용할 헤더
			.exposedHeaders("Authorization", "RefreshToken", "*")  // 브라우저에 노출할 헤더
			.allowCredentials(true)  // 쿠키 등 자격 증명 포함 허용
			.maxAge(3600);  // CORS preflight 요청 결과를 캐시하는 시간
	}
}