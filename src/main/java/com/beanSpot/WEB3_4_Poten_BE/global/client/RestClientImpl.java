package com.beanSpot.WEB3_4_Poten_BE.global.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RestClientImpl implements RestClient {

    private final RestTemplate restTemplate;

    public RestClientImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ResponseEntity<Map> get(String uri, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                Map.class
        );
    }
}