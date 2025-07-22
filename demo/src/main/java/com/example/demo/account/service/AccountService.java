package com.example.demo.account.service;

import com.example.demo.Api.ApiResponse;
import com.example.demo.account.controller.request.RegisterRequest;
import com.example.demo.account.controller.response.RegisterResponse;

public interface AccountService {
    ApiResponse<RegisterResponse> register(RegisterRequest request, String token);
}
