package com.example.demo.order.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long quantity;
    private Long totalPrice;
    private Timestamp createdAt;
    private boolean isReserved;
    private Long accountId;
    private Long breadId;


}
