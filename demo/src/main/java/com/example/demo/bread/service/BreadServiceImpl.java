package com.example.demo.bread.service;

import com.example.demo.bread.controller.response.BreadResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.bread.controller.request.BreadRegisterRequest;
import com.example.demo.bread.entity.Bread;
import com.example.demo.bread.repository.BreadRepository;
import com.example.demo.utils.CheckToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class BreadServiceImpl implements BreadService {
    private final BreadRepository breadRepository;
    private final AccountRepository accountRepository;
    private final CheckToken checkToken;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<BreadResponse>> getAllBreads() {
        List<Bread> breads = breadRepository.findAll();
        List<BreadResponse> response = breads.stream()
                .map(bread -> BreadResponse.builder()
                        .id(bread.getId())
                        .name(bread.getName())
                        .origin(bread.getOrigin())
                        .price(bread.getPrice())
                        .description(bread.getDescription())
                        .quantity(bread.getQuantity())
                        .accountId(bread.getAccount().getId())
                        .build()).toList();
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<BreadResponse> register(String token, BreadRegisterRequest request) {

        long accountId = Long.parseLong(checkToken.findAccountId(token));

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("계정을 찾을 수 없습니다."));

        Bread savedBread = breadRepository.save(
                Bread.builder()
                        .name(request.getName())
                        .sort(request.getSort())
                        .price(request.getPrice())
                        .quantity(request.getQuantity())
                        .description(request.getDescription())
                        .origin(request.getOrigin())
                        .account(account)
                        .build()
        );

        BreadResponse response = BreadResponse.builder()
                .id(savedBread.getId())
                .name(savedBread.getName())
                .sort(savedBread.getSort())
                .price(savedBread.getPrice())
                .quantity(savedBread.getQuantity())
                .description(savedBread.getDescription())
                .origin(savedBread.getOrigin())
                .accountId(account.getId())
                .build();

        return ApiResponse.success(response);
    }
}
