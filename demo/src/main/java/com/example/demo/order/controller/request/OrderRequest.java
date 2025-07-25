package com.example.demo.order.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long breadId;
    private Long quantity;
    private boolean isReserved;
}