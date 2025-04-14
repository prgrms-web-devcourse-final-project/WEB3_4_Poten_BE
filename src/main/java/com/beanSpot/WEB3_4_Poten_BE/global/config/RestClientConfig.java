package com.beanSpot.WEB3_4_Poten_BE.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 외부 API 호출에 사용할 RestClient Bean을 설정하는 구성 클래스
 *
 * @author -- 김남우 --
 * @since -- 4월 12일 --
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}