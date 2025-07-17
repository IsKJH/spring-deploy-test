package com.example.demo.kakao_authentication.controller;

import com.example.demo.kakao_authentication.controller.response.UserInfoResponse;
import com.example.demo.kakao_authentication.service.KakaoAuthenticationService;
import com.example.demo.kakao_authentication.service.KakaoAuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao-authentication")
public class KakaoAuthenticationController {
    private final KakaoAuthenticationService kakaoAuthenticationService;

    @GetMapping("/login")
    public ResponseEntity<UserInfoResponse> kakaoLogin(@RequestParam(value = "code", required = false) String code) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(kakaoAuthenticationService.getAccessCode()))
                    .build();
        } else {
            Map<String, Object> tokenResponse = kakaoAuthenticationService.getAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");
            Map<String, Object> userInfoResponse = kakaoAuthenticationService.getUserInfo(accessToken);

            Map<String, Object> properties = (Map<String, Object>) userInfoResponse.get("properties");
            Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoResponse.get("kakao_account");

            String nickname = (String) properties.get("nickname");
            String email = (String) kakaoAccount.get("email");
            String tempToken = (String) userInfoResponse.get("tempToken");

            UserInfoResponse response = new UserInfoResponse(accessToken, tempToken, nickname, email);
            return ResponseEntity.ok(response);
        }
    }
}
