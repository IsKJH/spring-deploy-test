package com.example.demo.order.service;

import com.example.demo.api.ApiResponse;
import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.bread.entity.Bread;
import com.example.demo.bread.repository.BreadRepository;
import com.example.demo.order.controller.request.OrderRequest;
import com.example.demo.order.controller.response.OrderResponse;
import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRespository;
    private final RedisCacheService redisCacheService;
    private final AccountRepository accountRepository;
    private final BreadRepository breadRepository;

    @Override
    public ApiResponse<OrderResponse> order(String token, OrderRequest request) {
        String userToken = token.replace("Bearer ", "");
        String accountIdStr = redisCacheService.getValueByKey(userToken, String.class);

        if (accountIdStr == null || accountIdStr.isEmpty()) {
            return ApiResponse.failure("토큰이 올바르지 않습니다.");
        }

        long accountId = Long.parseLong(accountIdStr);

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("계정을 찾을 수 없습니다."));
        long breadId = request.getBreadId();
        Bread bread = breadRepository.findById(breadId).orElseThrow(
                () -> new RuntimeException("빵을 찾을 수 없습니다."));

        Order savedOrder = orderRespository.save(Order.builder()
                .quantity(request.getQuantity())
                .totalPrice(bread.getPrice() * request.getQuantity())
                .isReserved(request.isReserved())
                .account(account)
                .bread(bread)
                .build()
        );

        OrderResponse response = OrderResponse.builder().
                id(savedOrder.getId()).
                quantity(savedOrder.getQuantity()).
                totalPrice(savedOrder.getTotalPrice()).
                createdAt(savedOrder.getCreatedAt()).
                isReserved(savedOrder.isReserved()).
                accountId(account.getId()).
                breadId(bread.getId()).
                build();

        return ApiResponse.success(response);
    }
}
