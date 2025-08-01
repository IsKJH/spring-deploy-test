package com.example.demo.electronics.controller;

import com.example.demo.electronics.controller.request.ElectronicsRegisterRequest;
import com.example.demo.electronics.controller.response.ElectronicsResponse;
import com.example.demo.electronics.service.ElectronicsService;
import com.example.demo.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/electronics")
@Transactional
public class ElectronicsController {
    private final ElectronicsService electronicsService;

    @PostMapping
    public ResponseEntity<ApiResponse<ElectronicsResponse>> register(@RequestHeader("Authorization") String token, @RequestBody ElectronicsRegisterRequest request) {
        ApiResponse<ElectronicsResponse> response = electronicsService.register(token, request);
        return ResponseEntity.ok(response);
    }

}
