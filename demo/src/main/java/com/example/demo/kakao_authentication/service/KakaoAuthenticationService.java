package com.example.demo.kakao_authentication.service;

import java.util.Map;

public interface KakaoAuthenticationService {
    String getAccessCode();
    Map<String, Object> getAccessToken(String code);
    Map<String, Object> getUserInfo(String token);
}
