package com.example.demo.naverSearch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NaverSearchServiceImpl implements NaverSearchService {

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    @Value("${kakao.map.api-key}")
    private String kakaoMapApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public NaverSearchServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object searchLocalPlaces(String query, String x, String y) {
        try {
            log.info("네이버 검색 API 호출 - 검색어: {}", query);
            
            // 이미 인코딩된 query를 직접 URL에 추가
            String url = "https://openapi.naver.com/v1/search/local.json" +
                    "?query=" + query +
                    "&display=10" +
                    "&start=1" +
                    "&sort=random";
            
            log.info("최종 요청 URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("네이버 API 응답: {}", response.getBody());
            JsonNode result = objectMapper.readValue(response.getBody(), JsonNode.class);
            log.info("파싱된 결과 - total: {}, items 개수: {}", 
                result.has("total") ? result.get("total").asInt() : "없음",
                result.has("items") ? result.get("items").size() : "없음");
            
            return result;

        } catch (Exception e) {
            log.error("네이버 검색 API 호출 실패: ", e);
            throw new RuntimeException("네이버 검색 API 호출 실패: " + e.getMessage());
        }
    }

    @Override
    public Object geocodeAddress(String address) {
        try {
            log.info("카카오 지도 API를 사용한 주소 검색 - 주소: {}", address);
            
            // 카카오 지도 API로 주소 검색
            String url = "https://dapi.kakao.com/v2/local/search/address.json" +
                    "?query=" + address;
            
            log.info("카카오 지도 API 요청 URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoMapApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("카카오 지도 API 응답: {}", response.getBody());
            JsonNode result = objectMapper.readValue(response.getBody(), JsonNode.class);
            JsonNode documents = result.get("documents");
            
            if (documents != null && documents.size() > 0) {
                JsonNode firstDoc = documents.get(0);
                double lat = firstDoc.get("y").asDouble(); 
                double lng = firstDoc.get("x").asDouble();
                
                // 지오코딩 API 형식으로 응답 구성 (기존 코드와 호환)
                Map<String, Object> geocodeResponse = Map.of(
                    "status", "OK",
                    "meta", Map.of("totalCount", 1),
                    "addresses", List.of(Map.of(
                        "roadAddress", firstDoc.has("road_address") && !firstDoc.get("road_address").isNull() 
                            ? firstDoc.get("road_address").get("address_name").asText() : "",
                        "jibunAddress", firstDoc.get("address_name").asText(),
                        "x", String.valueOf(lng),
                        "y", String.valueOf(lat)
                    ))
                );
                
                log.info("카카오 API로 변환된 좌표: {} -> ({}, {})", address, lat, lng);
                return objectMapper.valueToTree(geocodeResponse);
            } else {
                throw new RuntimeException("카카오 지도에서 주소를 찾을 수 없습니다: " + address);
            }

        } catch (Exception e) {
            log.error("카카오 지도 API 주소 변환 실패: ", e);
            
            // 카카오 API 실패 시 네이버 검색으로 대체
            log.info("카카오 API 실패, 네이버 검색으로 대체 시도");
            return geocodeAddressWithNaver(address);
        }
    }

    /**
     * 네이버 검색 API를 사용한 주소 좌표 변환 (백업용)
     */
    private Object geocodeAddressWithNaver(String address) {
        try {
            log.info("네이버 검색을 통한 주소 좌표 변환 - 주소: {}", address);
            
            Object searchResult = searchLocalPlaces(address, null, null);
            JsonNode result = objectMapper.valueToTree(searchResult);
            
            JsonNode items = result.get("items");
            if (items != null && items.size() > 0) {
                JsonNode firstItem = items.get(0);
                double lat = Integer.parseInt(firstItem.get("mapy").asText()) / 10000000.0;
                double lng = Integer.parseInt(firstItem.get("mapx").asText()) / 10000000.0;
                
                Map<String, Object> geocodeResponse = Map.of(
                    "status", "OK",
                    "meta", Map.of("totalCount", 1),
                    "addresses", List.of(Map.of(
                        "roadAddress", firstItem.get("roadAddress").asText(),
                        "jibunAddress", firstItem.get("address").asText(),
                        "x", String.valueOf(lng),
                        "y", String.valueOf(lat)
                    ))
                );
                
                log.info("네이버 검색으로 변환된 좌표: {} -> ({}, {})", address, lat, lng);
                return objectMapper.valueToTree(geocodeResponse);
            } else {
                throw new RuntimeException("주소를 찾을 수 없습니다: " + address);
            }

        } catch (Exception e) {
            log.error("네이버 주소 좌표 변환도 실패: ", e);
            throw new RuntimeException("주소 좌표 변환 실패: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> findMiddlePoint(List<String> addresses) {
        try {
            log.info("중간지점 계산 시작 - 주소 개수: {}", addresses.size());
            
            List<Map<String, Double>> coordinates = new ArrayList<>();
            
            // 각 주소를 좌표로 변환
            for (String address : addresses) {
                JsonNode geocodeResult = (JsonNode) geocodeAddress(address);
                JsonNode addressArray = geocodeResult.get("addresses");
                
                if (addressArray != null && addressArray.size() > 0) {
                    JsonNode firstAddress = addressArray.get(0);
                    double lat = Double.parseDouble(firstAddress.get("y").asText());
                    double lng = Double.parseDouble(firstAddress.get("x").asText());
                    
                    coordinates.add(Map.of("lat", lat, "lng", lng));
                    log.info("주소 '{}' -> 좌표: ({}, {})", address, lat, lng);
                }
            }
            
            if (coordinates.isEmpty()) {
                throw new RuntimeException("유효한 좌표를 찾을 수 없습니다.");
            }
            
            // 중간지점 계산 (단순 평균)
            double avgLat = coordinates.stream()
                    .mapToDouble(coord -> coord.get("lat"))
                    .average()
                    .orElse(0.0);
            
            double avgLng = coordinates.stream()
                    .mapToDouble(coord -> coord.get("lng"))
                    .average()
                    .orElse(0.0);
            
            log.info("계산된 중간지점: ({}, {})", avgLat, avgLng);
            
            return Map.of(
                "middlePoint", Map.of("lat", avgLat, "lng", avgLng),
                "inputCoordinates", coordinates
            );
            
        } catch (Exception e) {
            log.error("중간지점 계산 실패: ", e);
            throw new RuntimeException("중간지점 계산 실패: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> findNearbyPlaces(double lat, double lng, String category) {
        try {
            log.info("근처 장소 검색 - 좌표: ({}, {}), 카테고리: {}", lat, lng, category);
            
            // 중간지점 좌표 기반 지역명 검색으로 구체적인 근처 장소 찾기
            String locationQuery = getLocationFromCoordinates(lat, lng);
            
            String searchQuery;
            if (category.equals("subway")) {
                searchQuery = locationQuery + " 지하철역";
            } else {
                searchQuery = locationQuery + " 편의점";
            }
            
            log.info("지역 기반 검색 쿼리: {}", searchQuery);
            
            // 더 많은 결과를 가져와서 거리 계산 후 필터링
            String url = "https://openapi.naver.com/v1/search/local.json" +
                    "?query=" + searchQuery +
                    "&display=50" +
                    "&start=1" +
                    "&sort=random";
            
            log.info("근처 장소 검색 URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode result = objectMapper.readValue(response.getBody(), JsonNode.class);
            JsonNode items = result.get("items");
            
            log.info("검색 결과 - 총 개수: {}, 실제 아이템 수: {}", 
                result.has("total") ? result.get("total").asInt() : "없음",
                items != null ? items.size() : 0);
            
            if (items != null && items.size() > 0) {
                // 중간지점에서 가까운 순으로 정렬
                List<JsonNode> sortedPlaces = new ArrayList<>();
                for (JsonNode item : items) {
                    if (item.get("mapx") != null && item.get("mapy") != null) {
                        double placeLat = Integer.parseInt(item.get("mapy").asText()) / 10000000.0;
                        double placeLng = Integer.parseInt(item.get("mapx").asText()) / 10000000.0;
                        
                        // 실제 거리 계산 (하버사인 공식 사용)
                        double distance = calculateDistance(lat, lng, placeLat, placeLng);
                        
                        // 반경 2km 이내의 장소만 포함 (실제 근처만)
                        if (distance <= 2.0) {
                            // 거리 정보를 추가한 노드 생성
                            Map<String, Object> placeWithDistance = objectMapper.convertValue(item, Map.class);
                            placeWithDistance.put("distance", distance);
                            placeWithDistance.put("distanceText", String.format("%.1fkm", distance));
                            
                            sortedPlaces.add(objectMapper.valueToTree(placeWithDistance));
                        }
                    }
                }
                
                // 거리순 정렬
                sortedPlaces.sort((a, b) -> 
                    Double.compare(a.get("distance").asDouble(), b.get("distance").asDouble()));
                
                // 가까운 8개만 선택
                List<JsonNode> nearbyPlaces = sortedPlaces.stream()
                    .limit(8)
                    .collect(Collectors.toList());
                
                // 결과 구성
                Map<String, Object> nearbyResult = Map.of(
                    "lastBuildDate", result.get("lastBuildDate").asText(),
                    "total", nearbyPlaces.size(),
                    "start", 1,
                    "display", nearbyPlaces.size(),
                    "items", nearbyPlaces
                );
                
                log.info("중간지점 기준 근처 {}개 장소 검색 완료", nearbyPlaces.size());
                
                return Map.of(
                    "category", category,
                    "centerPoint", Map.of("lat", lat, "lng", lng),
                    "places", nearbyResult
                );
            } else {
                log.info("지역 기반 검색 결과가 없음. 전체 서울 검색으로 대체");
                
                // 지역 검색 결과가 없으면 서울 전체 검색으로 대체
                String fallbackQuery = category.equals("subway") ? "서울 지하철역" : "서울 편의점";
                String fallbackUrl = "https://openapi.naver.com/v1/search/local.json" +
                        "?query=" + fallbackQuery +
                        "&display=100" +
                        "&start=1" +
                        "&sort=random";
                
                log.info("대체 검색 URL: {}", fallbackUrl);
                
                ResponseEntity<String> fallbackResponse = restTemplate.exchange(
                        fallbackUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                
                JsonNode fallbackResult = objectMapper.readValue(fallbackResponse.getBody(), JsonNode.class);
                JsonNode fallbackItems = fallbackResult.get("items");
                
                if (fallbackItems != null && fallbackItems.size() > 0) {
                    log.info("대체 검색 결과 - 총 개수: {}", fallbackItems.size());
                    
                    // 거리 계산 및 필터링
                    List<JsonNode> sortedPlaces = new ArrayList<>();
                    for (JsonNode item : fallbackItems) {
                        if (item.get("mapx") != null && item.get("mapy") != null) {
                            double placeLat = Integer.parseInt(item.get("mapy").asText()) / 10000000.0;
                            double placeLng = Integer.parseInt(item.get("mapx").asText()) / 10000000.0;
                            
                            double distance = calculateDistance(lat, lng, placeLat, placeLng);
                            
                            // 반경 3km 이내의 장소만 포함 (대체 검색이므로 조금 더 넓게)
                            if (distance <= 3.0) {
                                Map<String, Object> placeWithDistance = objectMapper.convertValue(item, Map.class);
                                placeWithDistance.put("distance", distance);
                                placeWithDistance.put("distanceText", String.format("%.1fkm", distance));
                                
                                sortedPlaces.add(objectMapper.valueToTree(placeWithDistance));
                            }
                        }
                    }
                    
                    // 거리순 정렬
                    sortedPlaces.sort((a, b) -> 
                        Double.compare(a.get("distance").asDouble(), b.get("distance").asDouble()));
                    
                    // 가까운 8개만 선택
                    List<JsonNode> nearbyPlaces = sortedPlaces.stream()
                        .limit(8)
                        .collect(Collectors.toList());
                    
                    if (!nearbyPlaces.isEmpty()) {
                        Map<String, Object> nearbyResult = Map.of(
                            "lastBuildDate", fallbackResult.get("lastBuildDate").asText(),
                            "total", nearbyPlaces.size(),
                            "start", 1,
                            "display", nearbyPlaces.size(),
                            "items", nearbyPlaces
                        );
                        
                        log.info("대체 검색으로 {}개 근처 장소 찾음", nearbyPlaces.size());
                        
                        return Map.of(
                            "category", category,
                            "centerPoint", Map.of("lat", lat, "lng", lng),
                            "places", nearbyResult
                        );
                    }
                }
                
                // 모든 검색이 실패한 경우
                return Map.of(
                    "category", category,
                    "centerPoint", Map.of("lat", lat, "lng", lng),
                    "places", Map.of("items", List.of())
                );
            }
            
        } catch (Exception e) {
            log.error("근처 장소 검색 실패: ", e);
            throw new RuntimeException("근처 장소 검색 실패: " + e.getMessage());
        }
    }

    /**
     * 두 좌표 간의 실제 거리 계산 (하버사인 공식)
     * @param lat1 첫 번째 지점의 위도
     * @param lng1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lng2 두 번째 지점의 경도
     * @return 거리 (km)
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // 지구의 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // km 단위 거리
    }

    /**
     * 좌표를 기반으로 지역명 추출 (서울 지역 구 판별)
     * @param lat 위도  
     * @param lng 경도
     * @return 지역명
     */
    private String getLocationFromCoordinates(double lat, double lng) {
        // 강남역: 37.498095, 127.027610
        // 건대입구역: 37.540569, 127.069904
        // 중간지점: 37.519162, 127.048450 → 이 지역은 청담동/압구정동 근처 (강남구)
        
        log.info("좌표 기반 지역 판별: ({}, {})", lat, lng);
        
        // 강남구 지역 (강남역, 청담, 압구정, 삼성 등)
        if (lat >= 37.48 && lat <= 37.55 && lng >= 127.02 && lng <= 127.08) {
            return "강남구";
        }
        // 서초구 지역
        else if (lat >= 37.46 && lat <= 37.51 && lng >= 126.97 && lng <= 127.05) {
            return "서초구";
        }
        // 송파구 지역 (잠실 등)
        else if (lat >= 37.47 && lat <= 37.53 && lng >= 127.08 && lng <= 127.15) {
            return "송파구";
        }
        // 광진구 지역 (건대입구 등)
        else if (lat >= 37.53 && lat <= 37.57 && lng >= 127.06 && lng <= 127.10) {
            return "광진구";
        }
        // 성동구 지역
        else if (lat >= 37.54 && lat <= 37.58 && lng >= 127.02 && lng <= 127.07) {
            return "성동구";
        }
        // 중구 지역 (명동, 을지로 등) - 범위 확장
        else if (lat >= 37.54 && lat <= 37.58 && lng >= 126.97 && lng <= 127.02) {
            return "중구";
        }
        // 종로구 지역 - 범위 확장  
        else if (lat >= 37.54 && lat <= 37.60 && lng >= 126.97 && lng <= 127.02) {
            return "종로구";
        }
        // 용산구 지역
        else if (lat >= 37.52 && lat <= 37.55 && lng >= 126.96 && lng <= 127.01) {
            return "용산구";
        }
        // 기본값 (서울 전체 검색)
        else {
            return "서울";
        }
    }
}