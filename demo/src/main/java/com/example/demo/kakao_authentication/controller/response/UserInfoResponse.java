package com.example.demo.kakao_authentication.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String token;
    private String tempToken;
    private String nickname;
    private String email;
}
