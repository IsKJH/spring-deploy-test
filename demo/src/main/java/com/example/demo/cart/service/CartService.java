package com.example.demo.cart.service;

import com.example.demo.cart.controller.request.CartToOrderRequest;
import com.example.demo.order.controller.response.OrderResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.cart.controller.request.CartRequest;
import com.example.demo.cart.controller.response.CartResponse;

import java.util.List;

public interface CartService {
    ApiResponse<List<CartResponse>> getAllCart();

    ApiResponse<CartResponse> addCart(String token, CartRequest request);

    ApiResponse<OrderResponse> cartToOrder(String token, CartToOrderRequest request);
}
