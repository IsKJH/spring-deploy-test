package com.example.demo.electronics.service;

import com.example.demo.company.entity.Company;
import com.example.demo.company.repository.CompanyRepository;
import com.example.demo.electronics.controller.request.ElectronicsRegisterRequest;
import com.example.demo.electronics.controller.response.ElectronicsResponse;
import com.example.demo.electronics.entity.Electronics;
import com.example.demo.electronics.repository.ElectronicsRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.utils.CheckToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ElectronicsServiceImpl implements ElectronicsService {
    private final ElectronicsRepository electronicsRepository;
    private final CompanyRepository companyRepository;
    private final CheckToken checkToken;

    @Override
    public ApiResponse<ElectronicsResponse> register(String token, ElectronicsRegisterRequest request) {
        Long accountId = Long.parseLong(checkToken.findAccountId(token));

        Company company = companyRepository.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        Electronics savedElectronics = electronicsRepository.save(Electronics.builder().name(request.getName()).price(request.getPrice()).company(company).build());

        ElectronicsResponse response = ElectronicsResponse.builder().id(savedElectronics.getId()).name(savedElectronics.getName()).price(savedElectronics.getPrice()).companyName(savedElectronics.getCompany().getName()).companyId(savedElectronics.getCompany().getId()).build();

        return ApiResponse.success(response);
    }
}
