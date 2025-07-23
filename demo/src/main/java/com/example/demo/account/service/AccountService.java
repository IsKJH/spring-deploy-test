package com.example.demo.account.service;

import com.example.demo.api.ApiResponse;
import com.example.demo.account.controller.request.AccountRegisterRequest;
import com.example.demo.account.controller.response.AccountRegisterResponse;

public interface AccountService {
    ApiResponse<AccountRegisterResponse> register(AccountRegisterRequest request, String token);
}
