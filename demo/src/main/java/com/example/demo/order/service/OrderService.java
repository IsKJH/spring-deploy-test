package com.example.demo.order.service;

import com.example.demo.cart.controller.response.CartResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.order.controller.request.OrderRequest;
import com.example.demo.order.controller.response.OrderResponse;

import java.util.List;

public interface OrderService {
    ApiResponse<List<OrderResponse>> getAllOrder();


    ApiResponse<OrderResponse> addOrder(String token, OrderRequest request);

}
