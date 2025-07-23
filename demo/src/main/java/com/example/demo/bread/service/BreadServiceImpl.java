package com.example.demo.bread.service;

import com.example.demo.Api.ApiResponse;
import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.bread.controller.request.RegisterRequest;
import com.example.demo.bread.controller.response.RegisterResponse;
import com.example.demo.bread.entity.Bread;
import com.example.demo.bread.repository.BreadRepository;
import com.example.demo.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BreadServiceImpl implements BreadService {
    private final BreadRepository breadRepository;
    private final RedisCacheService redisCacheService;
    private final AccountRepository accountRepository;

    @Override
    public ApiResponse<RegisterResponse> register(RegisterRequest request, String token) {
        String tempToken = token.replace("Bearer ", "");
        String accountIdStr = redisCacheService.getValueByKey(tempToken, String.class);
        if (accountIdStr == null) {
            return ApiResponse.failure("토큰이 올바르지 않습니다.");
        }

        Long accountId = Long.parseLong(accountIdStr);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다."));
        Bread savedBread = breadRepository.save(
                Bread.builder()
                        .name(request.getName())
                        .sort(request.getSort())
                        .price(request.getPrice())
                        .quantity(request.getQuantity())
                        .description(request.getDescription())
                        .origin(request.getOrigin())
                        .accountId(account)
                        .build()
        );
        RegisterResponse response = RegisterResponse.builder()
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
