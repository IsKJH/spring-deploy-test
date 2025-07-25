package com.example.demo.naverSearch.service;

import java.util.List;
import java.util.Map;

public interface NaverSearchService {
    Object searchLocalPlaces(String query, String x, String y);
    Object geocodeAddress(String address);
    Map<String, Object> findMiddlePoint(List<String> addresses);
    Map<String, Object> findNearbyPlaces(double lat, double lng, String category);
}