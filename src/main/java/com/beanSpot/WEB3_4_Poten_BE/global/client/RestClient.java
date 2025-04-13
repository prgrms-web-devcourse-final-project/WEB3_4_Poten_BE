package com.beanSpot.WEB3_4_Poten_BE.global.client;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface RestClient {
    ResponseEntity<Map> get(String uri, String authorizationHeader);
}
