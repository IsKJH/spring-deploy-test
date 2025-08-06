package com.example.demo.company.service.request;

import com.example.demo.account.entity.Account;
import com.example.demo.company.entity.Company;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyRegisterRequest {
    private final String name;

    public Company toCompany(Account account) {
        return new Company(name, account);
    }
}
