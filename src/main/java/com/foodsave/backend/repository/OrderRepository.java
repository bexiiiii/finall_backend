package com.foodsave.backend.repository;

import com.foodsave.backend.entity.Order;
import com.foodsave.backend.entity.User;
import com.foodsave.backend.entity.Store;
import com.foodsave.backend.domain.enums.OrderStatus;
import com.foodsave.backend.domain.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    List<Order> findByStore(Store store);
    
    List<Order> findByStoreOrderByCreatedAtDesc(Store store);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    
    List<Order> findByStoreAndStatus(Store store, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.store = :store AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByStoreAndDateRange(
            @Param("store") Store store,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate")
    Page<Order> findByStatusAndDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.store = :store AND o.status = 'DELIVERED'")
    BigDecimal getTotalRevenueByStore(@Param("store") Store store);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    Long countOrdersByUser(@Param("user") User user);

    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.store.id = ?1 AND o.status = ?2")
    Page<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.paymentMethod = ?1")
    Page<Order> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.qrCode = ?1")
    Optional<Order> findByQrCode(String qrCode);

    List<Order> findByUserAndStore(User user, Store store);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByStoreAndCreatedAtBetween(Store store, LocalDateTime start, LocalDateTime end);
    List<Order> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByStore(Store store, Pageable pageable);
    Page<Order> findByUserAndStore(User user, Store store, Pageable pageable);

    Optional<Order> findByIdAndUser(Long id, User user);
    Optional<Order> findByIdAndStore(Long id, Store store);
    List<Order> findByUserIdAndStatus(Long userId, String status);
    List<Order> findByStoreIdAndUserId(Long storeId, Long userId);
    List<Order> findByStoreIdAndCreatedAtBetween(Long storeId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o")
    long count();

    @Query("SELECT SUM(o.total) FROM Order o")
    BigDecimal sumTotal();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.store = :store")
    long countByStore(@Param("store") Store store);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.store = :store")
    BigDecimal sumTotalByStore(@Param("store") Store store);

    @Query("SELECT AVG(o.total) FROM Order o WHERE o.store = :store")
    BigDecimal averageOrderValueByStore(@Param("store") Store store);

    @Query("SELECT o.status as status, COUNT(o) as count FROM Order o WHERE o.store = :store GROUP BY o.status")
    Map<OrderStatus, Long> countByStoreAndStatus(@Param("store") Store store);

    @Query("SELECT DATE(o.createdAt) as date, SUM(o.total) as total FROM Order o " +
           "WHERE o.store = :store AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(o.createdAt)")
    Map<LocalDateTime, BigDecimal> sumTotalByStoreAndDateRange(
        @Param("store") Store store,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(o.total) - SUM(oi.unitPrice * oi.quantity) FROM Order o JOIN o.items oi WHERE o.store = :store")
    BigDecimal sumDiscountByStore(@Param("store") Store store);

    @Query("SELECT DATE(o.createdAt) as date, SUM(o.total) - SUM(oi.unitPrice * oi.quantity) as discount FROM Order o " +
           "JOIN o.items oi WHERE o.store = :store AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(o.createdAt)")
    Map<LocalDateTime, BigDecimal> sumDiscountByStoreAndDateRange(
        @Param("store") Store store,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT AVG(o.total) - AVG(oi.unitPrice * oi.quantity) FROM Order o JOIN o.items oi WHERE o.store = :store")
    BigDecimal averageDiscountByStore(@Param("store") Store store);

    @Query("SELECT oi.product.id as productId, " +
           "SUM(oi.unitPrice * oi.quantity * (oi.product.discountPercentage / 100.0)) as discount " +
           "FROM Order o JOIN o.items oi WHERE o.store = :store " +
           "GROUP BY oi.product.id")
    Map<Long, BigDecimal> sumDiscountByStoreAndProduct(@Param("store") Store store);
}
