package com.example.demo.cart.controller.response;

import com.example.demo.account.entity.Account;
import com.example.demo.bread.entity.Bread;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private Long quantity;
    private Account account;
    private Bread bread;
}
