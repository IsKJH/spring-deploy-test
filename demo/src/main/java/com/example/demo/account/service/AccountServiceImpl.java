package com.example.demo.account.service;

import com.example.demo.Api.ApiResponse;
import com.example.demo.account.controller.request.RegisterRequest;
import com.example.demo.account.controller.response.RegisterResponse;
import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public ApiResponse<RegisterResponse> register(RegisterRequest request, String token) {
        String tempToken = token.replace("Bearer ", "");
        String accessToken = redisCacheService.getValueByKey(tempToken, String.class);
        if (accessToken == null) {
            return ApiResponse.failure("토큰이 올바르지 않습니다.");
        }

        Account savedAccount = accountRepository.save(
                Account.builder()
                        .nickname(request.getNickname())
                        .email(request.getEmail())
                        .build()
        );

        Long accountId = savedAccount.getId();
        String userToken = UUID.randomUUID().toString();

        redisCacheService.setKeyAndValue(userToken, accountId);
        redisCacheService.setKeyAndValue(accountId, accessToken);
        redisCacheService.deleteByKey(tempToken);

        RegisterResponse response = RegisterResponse.builder()
                .id(savedAccount.getId())
                .email(savedAccount.getEmail())
                .nickname(savedAccount.getNickname())
                .userToken(userToken)
                .build();

        return ApiResponse.success(response);
    }
}
