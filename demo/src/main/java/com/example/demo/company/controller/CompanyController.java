package com.example.demo.company.controller;

import com.example.demo.company.controller.request.CompanyRegisterRequest;
import com.example.demo.company.controller.response.CompanyResponse;
import com.example.demo.company.service.CompanyService;
import com.example.demo.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@Transactional
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> register(@RequestHeader("Authorization") String token, @RequestBody CompanyRegisterRequest request) {
        ApiResponse<CompanyResponse> response = companyService.register(token, request);
        return ResponseEntity.ok(response);
    }
}
