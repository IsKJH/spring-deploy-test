package com.example.demo.order.controller;

import com.example.demo.cart.controller.response.CartResponse;
import com.example.demo.order.controller.request.OrderRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.order.controller.response.OrderResponse;
import com.example.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        ApiResponse<List<OrderResponse>> response = orderService.getAllOrder();

        return ResponseEntity.ok(response);
    }


    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponse>> order(@RequestHeader("Authorization") String token, @RequestBody OrderRequest request) {
        ApiResponse<OrderResponse> response = orderService.addOrder(token, request);

        return ResponseEntity.ok(response);
    }
}
