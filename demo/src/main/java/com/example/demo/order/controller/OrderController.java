package com.example.demo.order.controller;

import com.example.demo.api.ApiResponse;
import com.example.demo.order.controller.request.OrderRequest;
import com.example.demo.order.controller.response.OrderResponse;
import com.example.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponse>> order(@RequestHeader("Authorization") String token, @RequestBody OrderRequest request) {
        ApiResponse<OrderResponse> response = orderService.order(token, request);

        return ResponseEntity.ok(response);
    }
}
