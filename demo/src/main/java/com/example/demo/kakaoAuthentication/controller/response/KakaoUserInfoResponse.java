package com.example.demo.kakaoAuthentication.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoUserInfoResponse {
    private String token;
    private String tempToken;
    private String nickname;
    private String email;
    private Boolean isNewUser;

    public static KakaoUserInfoResponse from(String token, String tempToken, String nickname, String email, Boolean isNewUser) {
        return KakaoUserInfoResponse.builder()
                .token(token)
                .tempToken(tempToken)
                .nickname(nickname)
                .email(email)
                .isNewUser(isNewUser)
                .build();
    }
}
