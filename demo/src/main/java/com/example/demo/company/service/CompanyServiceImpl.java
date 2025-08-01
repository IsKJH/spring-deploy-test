package com.example.demo.company.service;

import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.company.controller.request.CompanyRegisterRequest;
import com.example.demo.company.controller.response.CompanyResponse;
import com.example.demo.company.entity.Company;
import com.example.demo.company.repository.CompanyRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.utils.CheckToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final AccountRepository accountRepository;
    private final CheckToken checkToken;

    @Override
    public ApiResponse<CompanyResponse> register(String token, CompanyRegisterRequest request) {
        Long accountId = Long.parseLong(checkToken.findAccountId(token));
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다."));

        Company savedCompany = companyRepository.save(
                Company.builder().name(request.getName()).account(account).build()
        );

        CompanyResponse response = CompanyResponse.builder().id(savedCompany.getId()).name(savedCompany.getName()).accountId(savedCompany.getAccount().getId()).build();
        return ApiResponse.success(response);
    }
}

