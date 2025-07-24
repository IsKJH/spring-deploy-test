package com.example.demo.cart.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartToOrderRequest {
    private Long cartItemId;
    private boolean isReserved;
}