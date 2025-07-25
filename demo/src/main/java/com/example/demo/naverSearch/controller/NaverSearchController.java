package com.example.demo.naverSearch.controller;

import com.example.demo.naverSearch.service.NaverSearchService;
import com.example.demo.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/naver")
@RequiredArgsConstructor
public class NaverSearchController {

    private final NaverSearchService naverSearchService;

    @GetMapping("/search/local")
    public ResponseEntity<ApiResponse> searchPlaces(
            @RequestParam String query,
            @RequestParam(required = false) String x,
            @RequestParam(required = false) String y) {
        try {
            Object result = naverSearchService.searchLocalPlaces(query, x, y);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.failure("검색 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/geocode")
    public ResponseEntity<ApiResponse> geocodeAddress(@RequestParam String address) {
        try {
            Object result = naverSearchService.geocodeAddress(address);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.failure("주소 변환 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/middle-point")
    public ResponseEntity<ApiResponse> findMiddlePoint(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> addresses = request.get("addresses");
            if (addresses == null || addresses.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("주소 목록이 비어있습니다."));
            }
            
            Map<String, Object> result = naverSearchService.findMiddlePoint(addresses);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.failure("중간지점 계산 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/nearby-places")
    public ResponseEntity<ApiResponse> findNearbyPlaces(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam String category) {
        try {
            Map<String, Object> result = naverSearchService.findNearbyPlaces(lat, lng, category);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.failure("근처 장소 검색 실패: " + e.getMessage()));
        }
    }
}