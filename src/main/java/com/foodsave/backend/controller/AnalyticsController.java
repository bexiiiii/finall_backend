package com.foodsave.backend.controller;

import com.foodsave.backend.domain.enums.Permission;
import com.foodsave.backend.dto.AnalyticsDTO;
import com.foodsave.backend.security.annotation.RequirePermission;
import com.foodsave.backend.service.AnalyticsService;
import com.foodsave.backend.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    @RequirePermission(Permission.ANALYTICS_READ)
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        try {
            return ResponseEntity.ok(analyticsService.getAnalytics());
        } catch (Exception e) {
            throw new ApiException("Failed to fetch analytics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sales/{storeId}")
    @RequirePermission(Permission.ANALYTICS_READ)
    public ResponseEntity<AnalyticsDTO> getStoreSalesAnalytics(@PathVariable Long storeId) {
        try {
            return ResponseEntity.ok(analyticsService.getStoreSalesAnalytics(storeId));
        } catch (Exception e) {
            throw new ApiException("Failed to fetch store sales analytics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/{storeId}")
    @RequirePermission(Permission.ANALYTICS_READ)
    public ResponseEntity<AnalyticsDTO> getStoreProductsAnalytics(@PathVariable Long storeId) {
        try {
            return ResponseEntity.ok(analyticsService.getStoreProductsAnalytics(storeId));
        } catch (Exception e) {
            throw new ApiException("Failed to fetch store products analytics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/discounts/{storeId}")
    @RequirePermission(Permission.ANALYTICS_READ)
    public ResponseEntity<AnalyticsDTO> getStoreDiscountsAnalytics(@PathVariable Long storeId) {
        try {
            return ResponseEntity.ok(analyticsService.getStoreDiscountsAnalytics(storeId));
        } catch (Exception e) {
            throw new ApiException("Failed to fetch store discounts analytics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{storeId}")
    @RequirePermission(Permission.ANALYTICS_READ)
    public ResponseEntity<AnalyticsDTO> getStoreUsersAnalytics(@PathVariable Long storeId) {
        try {
            return ResponseEntity.ok(analyticsService.getStoreUsersAnalytics(storeId));
        } catch (Exception e) {
            throw new ApiException("Failed to fetch store users analytics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 