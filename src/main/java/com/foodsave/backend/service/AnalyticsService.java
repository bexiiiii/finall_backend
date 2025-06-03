package com.foodsave.backend.service;

import com.foodsave.backend.dto.AnalyticsDTO;
import com.foodsave.backend.entity.Order;
import com.foodsave.backend.entity.Product;
import com.foodsave.backend.entity.Store;
import com.foodsave.backend.repository.OrderRepository;
import com.foodsave.backend.repository.ProductRepository;
import com.foodsave.backend.repository.StoreRepository;
import com.foodsave.backend.repository.UserRepository;
import com.foodsave.backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public AnalyticsDTO getAnalytics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", orderRepository.count());
        data.put("totalProducts", productRepository.count());
        data.put("totalStores", storeRepository.count());
        data.put("totalUsers", userRepository.count());
        data.put("totalRevenue", orderRepository.sumTotal());
        return new AnalyticsDTO(data);
    }

    public AnalyticsDTO getStoreSalesAnalytics(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Map<String, Object> data = new HashMap<>();
        data.put("storeId", storeId);
        data.put("storeName", store.getName());
        data.put("totalOrders", orderRepository.countByStore(store));
        data.put("totalRevenue", orderRepository.sumTotalByStore(store));
        data.put("averageOrderValue", orderRepository.averageOrderValueByStore(store));
        data.put("ordersByStatus", orderRepository.countByStoreAndStatus(store));
        data.put("revenueByDay", orderRepository.sumTotalByStoreAndDateRange(store, 
            LocalDateTime.now().minusDays(30), LocalDateTime.now()));
        
        return new AnalyticsDTO(data);
    }

    public AnalyticsDTO getStoreProductsAnalytics(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Map<String, Object> data = new HashMap<>();
        data.put("storeId", storeId);
        data.put("storeName", store.getName());
        data.put("totalProducts", productRepository.countByStore(store));
        data.put("productsByCategory", productRepository.countByStoreAndCategory(store));
        data.put("topSellingProducts", productRepository.findTopSellingByStore(store, PageRequest.of(0, 10)).getContent());
        data.put("lowStockProducts", productRepository.findLowStockByStore(store, PageRequest.of(0, 10)).getContent());
        data.put("productsByStatus", productRepository.countByStoreAndStatus(store));
        
        return new AnalyticsDTO(data);
    }

    public AnalyticsDTO getStoreDiscountsAnalytics(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Map<String, Object> data = new HashMap<>();
        data.put("storeId", storeId);
        data.put("storeName", store.getName());
        data.put("totalDiscounts", orderRepository.sumDiscountByStore(store));
        data.put("discountsByDay", orderRepository.sumDiscountByStoreAndDateRange(store, 
            LocalDateTime.now().minusDays(30), LocalDateTime.now()));
        data.put("averageDiscount", orderRepository.averageDiscountByStore(store));
        data.put("discountsByProduct", orderRepository.sumDiscountByStoreAndProduct(store));
        
        return new AnalyticsDTO(data);
    }

    public AnalyticsDTO getStoreUsersAnalytics(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Map<String, Object> data = new HashMap<>();
        data.put("storeId", storeId);
        data.put("storeName", store.getName());
        data.put("totalCustomers", userRepository.countByStore(store));
        data.put("newCustomers", userRepository.countNewByStore(store, 
            LocalDateTime.now().minusDays(30)));
        data.put("activeCustomers", userRepository.countActiveByStore(store));
        data.put("customersByOrderCount", userRepository.countByStoreAndOrderCount(store));
        data.put("averageOrderValueByCustomer", userRepository.averageOrderValueByStore(store));
        
        return new AnalyticsDTO(data);
    }

    public Map<String, Double> getSalesByCategory(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Map<String, Double> getSalesByProduct(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Map<String, Double> getSalesByDay(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Map<String, Double> getSalesByMonth(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Map<String, Double> getSalesByYear(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<Product> getTopSellingProducts(Long storeId, LocalDateTime startDate, LocalDateTime endDate, int limit) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<Order> getRecentOrders(Long storeId, int limit) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public double getTotalSales(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public double getAverageOrderValue(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public int getTotalOrders(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }
} 