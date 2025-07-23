package com.example.demo.bread.service;

import com.example.demo.api.ApiResponse;
import com.example.demo.bread.controller.request.BreadRegisterRequest;
import com.example.demo.bread.controller.response.BreadRegisterResponse;

public interface BreadService {
    ApiResponse<BreadRegisterResponse> register(String token, BreadRegisterRequest request);
}
