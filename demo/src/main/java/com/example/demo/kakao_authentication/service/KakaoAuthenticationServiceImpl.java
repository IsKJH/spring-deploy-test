package com.example.demo.kakao_authentication.service;

import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.kakao_authentication.controller.response.KakaoUserInfoResponse;
import com.example.demo.kakao_authentication.repository.KakaoAuthenticationRepository;
import com.example.demo.redis_cache.service.RedisCacheService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {
    private final KakaoAuthenticationRepository kakaoAuthenticationRepository;
    private final AccountRepository accountRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public ResponseEntity<KakaoUserInfoResponse> handleLogin(@Nullable String code) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(kakaoAuthenticationRepository.getAccessCode()))
                    .build();
        } else {
            Map<String, Object> tokenResponse = kakaoAuthenticationRepository.getAccessToken(code);
            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String token = tokenResponse.get("access_token").toString();

            Map<String, Object> userInfo = kakaoAuthenticationRepository.getUserInfo(token);
            if (userInfo != null) {
                String tempToken = createTemporaryUserToken(token);

                Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
                Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
                if (properties == null || kakaoAccount == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                String nickname = (String) properties.get("nickname");
                String email = (String) kakaoAccount.get("email");

                if (nickname == null || email == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                Optional<Account> existAccount = accountRepository.findByEmail(email);
                log.info("Exist account: {}", existAccount);
                if (existAccount.isPresent()) {
                    return ResponseEntity.ok(
                            KakaoUserInfoResponse.from(token, tempToken, nickname, email, false));
                }
                return ResponseEntity.ok(
                        KakaoUserInfoResponse.from(token, tempToken, nickname, email, true));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String createTemporaryUserToken(String accessToken) {
        String token = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(token, accessToken, Duration.ofMinutes(5));
        return token;
    }
}
