package com.example.demo.kakao_authentication.service;

import com.example.demo.kakao_authentication.controller.response.KakaoUserInfoResponse;
import org.springframework.http.ResponseEntity;


public interface KakaoAuthenticationService {
    ResponseEntity<KakaoUserInfoResponse> handleLogin(String code);
}
