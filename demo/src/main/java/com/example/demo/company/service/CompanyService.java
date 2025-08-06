package com.example.demo.company.service;

import com.example.demo.company.controller.response_form.CompanyRegisterResponseForm;
import com.example.demo.company.service.request.CompanyRegisterRequest;
import com.example.demo.response.ApiResponse;

public interface CompanyService {
    ApiResponse<CompanyRegisterResponseForm> register(String token, CompanyRegisterRequest request);
}
