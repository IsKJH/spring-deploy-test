package com.example.demo.account.controller.response;

import com.example.demo.kakao_authentication.controller.response.KakaoUserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long id;
    private String email;
    private String nickname;
    private String userToken;
}
