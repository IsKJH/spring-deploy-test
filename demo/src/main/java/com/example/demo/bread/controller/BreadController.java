package com.example.demo.bread.controller;

import com.example.demo.api.ApiResponse;
import com.example.demo.bread.controller.request.BreadRegisterRequest;
import com.example.demo.bread.controller.response.BreadRegisterResponse;
import com.example.demo.bread.service.BreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bread")
@Slf4j
public class BreadController {
    private final BreadService breadService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<BreadRegisterResponse>> register(@RequestHeader("Authorization") String token, @RequestBody BreadRegisterRequest request) {
        ApiResponse<BreadRegisterResponse> response = breadService.register(token, request);

        return ResponseEntity.ok(response);
    }
}
