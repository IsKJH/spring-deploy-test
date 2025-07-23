package com.example.demo.account.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRegisterResponse {
    private Long id;
    private String email;
    private String nickname;
    private String userToken;
}
