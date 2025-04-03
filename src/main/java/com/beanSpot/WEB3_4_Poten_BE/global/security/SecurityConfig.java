package com.beanSpot.WEB3_4_Poten_BE.global.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtAuthenticationFilter;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.service.MemberService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.CustomAuthorizationRequestResolver;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.CustomOAuth2UserService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.OAuth2SuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	private final OAuth2AuthorizedClientService authorizedClientService;
	private final JwtService jwtService;
	private final MemberService memberService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomAuthorizationRequestResolver authorizationRequestResolver;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtService, memberService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 인증 관련 공개 엔드포인트
				.requestMatchers("/admin/login").permitAll()
				.requestMatchers("/oauth2/**").permitAll()
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/refresh").permitAll()
				.requestMatchers("/api/admin/login").permitAll() // Admin 로그인 엔드포인트 추가

				// API 문서 관련 공개 엔드포인트
				.requestMatchers("/swagger-ui/**").permitAll()
				.requestMatchers("/v3/api-docs/**").permitAll()

				.requestMatchers("/reservation/payment/api/confirm").permitAll()

				// 카페 조회 관련 공개 엔드포인트 (GET 메소드만 허용)
				.requestMatchers(HttpMethod.GET, "/api/cafes/**").permitAll()

				// 관리자 엔드포인트 접근 제한
				.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
				.requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

				// 카페 주인 엔드포인트 접근 제한 - OWNER 권한을 가진 사용자만 접근 가능
				.requestMatchers(HttpMethod.POST, "/api/cafes/**").hasAuthority("ROLE_OWNER")
				.requestMatchers(HttpMethod.PUT, "/api/cafes/**").hasAuthority("ROLE_OWNER")
				.requestMatchers(HttpMethod.DELETE, "/api/cafes/**").hasAuthority("ROLE_OWNER")

				// 예약 관련 엔드포인트는 인증된 사용자만 접근
				.requestMatchers("/reservations/**").authenticated()

				// 그 외 모든 요청은 인증 필요
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/admin/login") // 로그인 페이지 URL
				.loginProcessingUrl("/api/admin/login") // 로그인 처리 URL
				.usernameParameter("email") // 이메일 파라미터 이름
				.passwordParameter("password") // 비밀번호 파라미터 이름
				.successHandler((request, response, authentication) -> {
					response.setContentType("application/json");
					response.getWriter().write("{\"success\":true,\"message\":\"로그인 성공\"}");
				})
				.failureHandler((request, response, exception) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.getWriter().write("{\"success\":false,\"message\":\"로그인 실패: "
						+ exception.getMessage() + "\"}");
				})
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"message\":\"인증이 필요합니다.\",\"code\":\"UNAUTHORIZED\"}");
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"message\":\"접근 권한이 없습니다.\",\"code\":\"FORBIDDEN\"}");
				})
			)
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.authorizationRequestResolver(authorizationRequestResolver)
				)
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2SuccessHandler())
			)
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(Arrays.asList("Authorization", "RefreshToken"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public OAuth2SuccessHandler oAuth2SuccessHandler() {
		return new OAuth2SuccessHandler(jwtService, authorizedClientService);
	}
}