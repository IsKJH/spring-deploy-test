package com.example.demo.cart.controller;

import com.example.demo.cart.controller.request.CartToOrderRequest;
import com.example.demo.order.controller.response.OrderResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.cart.controller.request.CartRequest;
import com.example.demo.cart.controller.response.CartResponse;
import com.example.demo.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CartResponse>>> getAllCarts() {
        ApiResponse<List<CartResponse>> response = cartService.getAllCart();

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<CartResponse>> addCart(@RequestHeader("Authorization") String token, @RequestBody CartRequest request) {
        ApiResponse<CartResponse> response = cartService.addCart(token, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/toOrder")
    public ResponseEntity<ApiResponse<OrderResponse>> toOrder(@RequestHeader("Authorization") String token, @RequestBody CartToOrderRequest request) {
        ApiResponse<OrderResponse> response = cartService.cartToOrder(token, request);

        return ResponseEntity.ok(response);
    }
}
