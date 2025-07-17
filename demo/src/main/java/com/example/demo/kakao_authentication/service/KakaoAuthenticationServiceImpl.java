package com.example.demo.kakao_authentication.service;

import com.example.demo.kakao_authentication.repository.KakaoAuthenticationRepository;
import com.example.demo.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {
    private final KakaoAuthenticationRepository kakaoAuthenticationRepository;

    private final RedisCacheService redisCacheService;

    @Override
    public String getAccessCode() {
        return kakaoAuthenticationRepository.getAccessCode();
    }

    @Override
    public Map<String, Object> getAccessToken(String code) {
        return kakaoAuthenticationRepository.getAccessToken(code);
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        Map<String, Object> userInfo = kakaoAuthenticationRepository.getUserInfo(token);
        if (userInfo != null) {
            String tempToken = createTemporaryUserToken(token);
            userInfo.put("tempToken", tempToken);
        }
        return userInfo;
    }

    private String createTemporaryUserToken(String accessToken) {
        String token = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(token, accessToken, Duration.ofMinutes(5));
        return token;
    }
}
