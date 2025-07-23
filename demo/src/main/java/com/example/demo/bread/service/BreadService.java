package com.example.demo.bread.service;

import com.example.demo.Api.ApiResponse;
import com.example.demo.bread.controller.request.RegisterRequest;
import com.example.demo.bread.controller.response.RegisterResponse;

public interface BreadService {
    ApiResponse<RegisterResponse> register(RegisterRequest request, String token);
}
