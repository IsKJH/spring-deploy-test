package com.example.demo.electronics.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectronicsRegisterRequest {
    private String name;
    private Long price;
}
