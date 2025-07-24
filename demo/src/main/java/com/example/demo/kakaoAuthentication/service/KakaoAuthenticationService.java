package com.example.demo.kakaoAuthentication.service;

import com.example.demo.kakaoAuthentication.controller.response.KakaoUserInfoResponse;
import org.springframework.http.ResponseEntity;


public interface KakaoAuthenticationService {
    ResponseEntity<KakaoUserInfoResponse> handleLogin(String code);
}
