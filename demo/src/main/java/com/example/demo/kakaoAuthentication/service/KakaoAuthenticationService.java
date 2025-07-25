package com.example.demo.kakaoAuthentication.service;

import com.example.demo.kakaoAuthentication.controller.response.KakaoUserInfoResponse;
import org.springframework.http.ResponseEntity;


public interface KakaoAuthenticationService {
    ResponseEntity<String> getLoginUrl();
    ResponseEntity<KakaoUserInfoResponse> handleLogin(String code);
    ResponseEntity<String> handleFrontLogin(String code);
}
