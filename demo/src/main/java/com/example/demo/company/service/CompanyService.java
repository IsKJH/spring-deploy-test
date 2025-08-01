package com.example.demo.company.service;

import com.example.demo.company.controller.request.CompanyRegisterRequest;
import com.example.demo.company.controller.response.CompanyResponse;
import com.example.demo.response.ApiResponse;

public interface CompanyService {
    ApiResponse<CompanyResponse> register(String token, CompanyRegisterRequest request);
}
