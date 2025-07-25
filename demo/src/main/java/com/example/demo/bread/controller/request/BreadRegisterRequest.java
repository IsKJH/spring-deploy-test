package com.example.demo.bread.controller.request;

import com.example.demo.bread.entity.enums.Sort;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreadRegisterRequest {
    private String name;
    private Sort sort;
    private Long price;
    private Long quantity;
    private String description;
    private String origin;
}
