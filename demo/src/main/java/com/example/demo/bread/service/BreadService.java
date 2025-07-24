package com.example.demo.bread.service;

import com.example.demo.bread.controller.response.BreadResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.bread.controller.request.BreadRegisterRequest;

import java.util.List;

public interface BreadService {
    ApiResponse<List<BreadResponse>> getAllBreads();
    ApiResponse<BreadResponse> register(String token, BreadRegisterRequest request);
}
