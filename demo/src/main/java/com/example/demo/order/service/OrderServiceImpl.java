package com.example.demo.order.service;

import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.cart.controller.response.CartResponse;
import com.example.demo.cart.entity.Cart;
import com.example.demo.response.ApiResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RedisCacheService redisCacheService;
    private final AccountRepository accountRepository;
    private final BreadRepository breadRepository;

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> response = orders.stream()
                .map(order -> OrderResponse.builder()
                        .id(order.getId())
                        .quantity(order.getQuantity())
                        .totalPrice(order.getTotalPrice())
                        .createdAt(order.getCreatedAt())
                        .isReserved(order.isReserved())
                        .accountId(order.getAccount().getId())
                        .breadId(order.getBread().getId())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<OrderResponse> addOrder(String token, OrderRequest request) {
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

        Order savedOrder = orderRepository.save(Order.builder()
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
