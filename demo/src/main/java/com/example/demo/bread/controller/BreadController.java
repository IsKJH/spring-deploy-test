package com.example.demo.bread.controller;

import com.example.demo.response.ApiResponse;
import com.example.demo.bread.controller.request.BreadRegisterRequest;
import com.example.demo.bread.controller.response.BreadResponse;
import com.example.demo.bread.service.BreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bread")
@Slf4j
public class BreadController {
    private final BreadService breadService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BreadResponse>>> getAllBread() {
        ApiResponse<List<BreadResponse>> response = breadService.getAllBreads();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<BreadResponse>> register(@RequestHeader("Authorization") String token, @RequestBody BreadRegisterRequest request) {
        ApiResponse<BreadResponse> response = breadService.register(token, request);

        return ResponseEntity.ok(response);
    }
}
