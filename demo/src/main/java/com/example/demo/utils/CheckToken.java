package com.example.demo.utils;

import com.example.demo.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CheckToken {
    private final RedisCacheService redisCacheService;

    public String findAccountId(String token) {
        String userToken = replaceToken(token);
        String accountIdStr = redisCacheService.getValueByKey(userToken, String.class);
        if (accountIdStr == null || accountIdStr.isEmpty()) {
            return "토큰이 올바르지 않습니다.";
        }
        return accountIdStr;
    }

    public String replaceToken(String token) {
        return token.replace("Bearer ", "");
    }
}
