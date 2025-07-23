package com.example.demo.order.service;

import com.example.demo.api.ApiResponse;
import com.example.demo.order.controller.request.OrderRequest;
import com.example.demo.order.controller.response.OrderResponse;

public interface OrderService {
    ApiResponse<OrderResponse> order(String token, OrderRequest request);
}
