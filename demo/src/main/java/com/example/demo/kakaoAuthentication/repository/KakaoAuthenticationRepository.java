package com.example.demo.kakaoAuthentication.repository;

import java.util.Map;

public interface KakaoAuthenticationRepository {
    String getAccessCode();

    String getFrontAccessCode();

    Map<String, Object> getAccessToken(String code);
    
    Map<String, Object> getFrontAccessToken(String code);
    
    Map<String, Object> getUserInfo(String token);
}
