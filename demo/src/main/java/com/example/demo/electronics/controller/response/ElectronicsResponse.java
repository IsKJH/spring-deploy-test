package com.example.demo.electronics.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectronicsResponse {
    private Long id;
    private String name;
    private Long price;
    private String companyName;
    private Long companyId;
}
