package com.example.demo.book.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String title;
    private String content;
    private Long price;
    private String publisher;
    private String author;
    private String category;
    private String description;
}
