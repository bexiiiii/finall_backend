package com.foodsave.backend.repository;

import com.foodsave.backend.entity.User;
import com.foodsave.backend.entity.Store;
import com.foodsave.backend.domain.enums.UserRole;
import com.foodsave.backend.domain.enums.Role;
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
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    
    @Query("SELECT u FROM User u JOIN u.stores s WHERE s.id = :storeId")
    List<User> findByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT u FROM User u WHERE u.role = ?1 AND u.stores IS EMPTY")
    List<User> findUnassignedStoreManagers(UserRole role);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findAllActive();

    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String token);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true")
    Optional<User> findActiveByRole(@Param("role") String role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND u IN " +
           "(SELECT DISTINCT o.user FROM Order o WHERE o.store = :store)")
    long countByStore(@Param("store") Store store);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND u IN " +
           "(SELECT DISTINCT o.user FROM Order o WHERE o.store = :store) " +
           "AND u.createdAt >= :since")
    long countNewByStore(@Param("store") Store store, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND u.active = true AND u IN " +
           "(SELECT DISTINCT o.user FROM Order o WHERE o.store = :store)")
    long countActiveByStore(@Param("store") Store store);

    @Query("SELECT COUNT(o) as orderCount, COUNT(DISTINCT o.user) as userCount " +
           "FROM Order o WHERE o.store = :store GROUP BY o.user")
    Map<Integer, Long> countByStoreAndOrderCount(@Param("store") Store store);

    @Query("SELECT AVG(o.total) FROM Order o WHERE o.store = :store GROUP BY o.user")
    BigDecimal averageOrderValueByStore(@Param("store") Store store);

    Page<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);
    List<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
}
