package com.example.demo.kakaoAuthentication.service;

import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.kakaoAuthentication.controller.response.KakaoUserInfoResponse;
import com.example.demo.kakaoAuthentication.repository.KakaoAuthenticationRepository;
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
    public ResponseEntity<String> getLoginUrl() {
        return ResponseEntity.ok(kakaoAuthenticationRepository.getFrontAccessCode());
    }

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
                if (existAccount.isPresent()) {
                    Long accountId = existAccount.get().getId();
                    String userToken = UUID.randomUUID().toString();

                    redisCacheService.setKeyAndValue(userToken, accountId);
                    redisCacheService.setKeyAndValue(accountId, tempToken);
                    redisCacheService.deleteByKey(tempToken);
                    return ResponseEntity.ok(
                            KakaoUserInfoResponse.from(token, userToken, nickname, email, false));
                }
                return ResponseEntity.ok(
                        KakaoUserInfoResponse.from(token, tempToken, nickname, email, true));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<String> handleFrontLogin(@Nullable String code) {
        // code가 없으면 카카오 로그인 페이지로 리다이렉트
        if (code == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(kakaoAuthenticationRepository.getFrontAccessCode()))
                    .build();
        }

        // 카카오 액세스 토큰 발급
        Map<String, Object> tokenResponse = kakaoAuthenticationRepository.getFrontAccessToken(code);
        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            return createErrorHtml("토큰 발급에 실패했습니다.");
        }
        
        String token = tokenResponse.get("access_token").toString();
        
        // 카카오 사용자 정보 조회
        Map<String, Object> userInfo = kakaoAuthenticationRepository.getUserInfo(token);
        if (userInfo == null) {
            return createErrorHtml("사용자 정보를 가져올 수 없습니다.");
        }

        // 사용자 정보 파싱
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        
        if (properties == null || kakaoAccount == null) {
            return createErrorHtml("사용자 정보가 올바르지 않습니다.");
        }

        String nickname = (String) properties.get("nickname");
        String email = (String) kakaoAccount.get("email");

        if (nickname == null || email == null) {
            return createErrorHtml("필수 사용자 정보가 누락되었습니다.");
        }

        // 기존 사용자 확인 및 토큰 생성
        String tempToken = createTemporaryUserToken(token);
        Optional<Account> existAccount = accountRepository.findByEmail(email);
        boolean isNewUser = existAccount.isEmpty();
        String userToken = tempToken;

        if (!isNewUser) {
            // 기존 사용자 - 실제 유저 토큰 생성
            Long accountId = existAccount.get().getId();
            userToken = UUID.randomUUID().toString();
            
            redisCacheService.setKeyAndValue(userToken, accountId);
            redisCacheService.setKeyAndValue(accountId, tempToken);
            redisCacheService.deleteByKey(tempToken);
        }

        return createSuccessHtml(token, userToken, nickname, email, isNewUser);
    }

    private ResponseEntity<String> createSuccessHtml(String token, String userToken, String nickname, String email, boolean isNewUser) {
        String htmlResponse = """
                <html>
                  <body>
                    <script>
                      window.opener.postMessage({
                        type: 'KAKAO_LOGIN_SUCCESS',
                        data: {
                          token: '%s',
                          tempToken: '%s',
                          nickname: '%s',
                          email: '%s',
                          isNewUser: %s
                        }
                      }, 'http://localhost:5173');
                      window.close();
                    </script>
                  </body>
                </html>
                """.formatted(token, userToken, nickname, email, isNewUser);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }

    private ResponseEntity<String> createErrorHtml(String errorMessage) {
        String htmlResponse = """
                <html>
                  <body>
                    <script>
                      window.opener.postMessage({
                        type: 'KAKAO_LOGIN_ERROR',
                        data: {
                          error: '%s'
                        }
                      }, 'http://localhost:5173');
                      window.close();
                    </script>
                  </body>
                </html>
                """.formatted(errorMessage);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }

    private String createTemporaryUserToken(String accessToken) {
        String token = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(token, accessToken, Duration.ofMinutes(5));
        return token;
    }
}
