package com.example.demo.company.service.response;

import com.example.demo.company.entity.Company;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyRegisterResponse {
    private final String name;
    private final Long accountId;

    public static CompanyRegisterResponse from(final Company company) {
        return new CompanyRegisterResponse(
                company.getName(),
                company.getAccount().getId()
        );
    }
}
