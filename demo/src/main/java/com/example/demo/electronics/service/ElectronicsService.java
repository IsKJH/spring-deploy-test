package com.example.demo.electronics.service;

import com.example.demo.electronics.controller.request.ElectronicsRegisterRequest;
import com.example.demo.electronics.controller.response.ElectronicsResponse;
import com.example.demo.response.ApiResponse;

public interface ElectronicsService {
    ApiResponse<ElectronicsResponse> register(String token, ElectronicsRegisterRequest request);
}
