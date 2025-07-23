package com.example.demo.account.controller;

import com.example.demo.api.ApiResponse;
import com.example.demo.account.controller.request.AccountRegisterRequest;
import com.example.demo.account.controller.response.AccountRegisterResponse;
import com.example.demo.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountRegisterResponse>> register(@RequestHeader("Authorization") String token, @RequestBody AccountRegisterRequest request) {
        ApiResponse<AccountRegisterResponse> response = accountService.register(request, token);

        return ResponseEntity.ok(response);
    }
}
