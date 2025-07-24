package com.example.demo.bread.controller.response;

import com.example.demo.bread.entity.enums.Sort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreadResponse {
    private Long id;
    private String name;
    private Sort sort;
    private Long price;
    private Long quantity;
    private String description;
    private String origin;
    private Long accountId;
}
