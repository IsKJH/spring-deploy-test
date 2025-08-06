package com.example.demo.company.controller;

import com.example.demo.company.controller.request_form.CompanyRegisterRequestForm;
import com.example.demo.company.controller.response_form.CompanyRegisterResponseForm;
import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.request.CompanyRegisterRequest;
import com.example.demo.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyRegisterResponseForm>> register(@RequestHeader("Authorization") String token, @RequestBody CompanyRegisterRequestForm requestForm) {
        CompanyRegisterRequest request = requestForm.toRequest();
        ApiResponse<CompanyRegisterResponseForm> response = companyService.register(token, request);
        return ResponseEntity.ok(response);
    }
}
