package com.example.demo.company.controller.request_form;

import com.example.demo.company.service.request.CompanyRegisterRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyRegisterRequestForm {
    private final String name;

    public CompanyRegisterRequest toRequest() {
        return new CompanyRegisterRequest(name);
    }
}
