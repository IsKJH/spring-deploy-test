package com.example.demo.bread.controller;

import com.example.demo.Api.ApiResponse;
import com.example.demo.bread.controller.request.RegisterRequest;
import com.example.demo.bread.controller.response.RegisterResponse;
import com.example.demo.bread.service.BreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Bread")
@Slf4j
public class BreadController {
    private final BreadService breadService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestHeader("Authorization") String token, @RequestBody RegisterRequest request) {
        ApiResponse<RegisterResponse> response = breadService.register(request, token);

        return ResponseEntity.ok(response);
    }
}
