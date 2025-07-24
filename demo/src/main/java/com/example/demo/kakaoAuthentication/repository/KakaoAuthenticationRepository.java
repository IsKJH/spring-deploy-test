package com.example.demo.kakaoAuthentication.repository;

import java.util.Map;

public interface KakaoAuthenticationRepository {
    String getAccessCode();

    Map<String, Object> getAccessToken(String code);

    Map<String, Object> getUserInfo(String token);
}
