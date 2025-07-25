package com.example.demo.kakaoAuthentication.controller;

import com.example.demo.kakaoAuthentication.controller.response.KakaoUserInfoResponse;
import com.example.demo.kakaoAuthentication.service.KakaoAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao-authentication")
public class KakaoAuthenticationController {
    private final KakaoAuthenticationService kakaoAuthenticationService;

    @GetMapping("/url")
    public ResponseEntity<String> loginUrl() {
        return kakaoAuthenticationService.getLoginUrl();
    }

    @GetMapping("/login")
    public ResponseEntity<KakaoUserInfoResponse> kakaoLogin(@RequestParam(value = "code", required = false) String code) {
        return kakaoAuthenticationService.handleLogin(code);
    }

    @GetMapping("/front-login")
    public ResponseEntity<String> kakaoFrontLogin(@RequestParam(value = "code", required = false) String code) {
        return kakaoAuthenticationService.handleFrontLogin(code);
    }
}
