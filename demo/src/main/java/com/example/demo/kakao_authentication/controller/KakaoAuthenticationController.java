package com.example.demo.kakao_authentication.controller;

import com.example.demo.kakao_authentication.controller.response.KakaoUserInfoResponse;
import com.example.demo.kakao_authentication.service.KakaoAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao-authentication")
public class KakaoAuthenticationController {
    private final KakaoAuthenticationService kakaoAuthenticationService;

    @GetMapping("/login")
    public ResponseEntity<KakaoUserInfoResponse> kakaoLogin(@RequestParam(value = "code", required = false) String code) {
        return kakaoAuthenticationService.handleLogin(code);
    }
}
