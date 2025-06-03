package com.foodsave.backend.security;

import com.foodsave.backend.entity.User;
import com.foodsave.backend.domain.enums.Permission;
import com.foodsave.backend.domain.enums.UserRole;
import com.foodsave.backend.security.annotation.RequirePermission;
import com.foodsave.backend.exception.AccessDeniedException;
import com.foodsave.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionHandler {

    private final UserRepository userRepository;

    public boolean hasPermission(Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No authenticated user found");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AccessDeniedException("User not found"));
        return hasPermission(user, permission);
    }

    public boolean hasPermission(User user, Permission permission) {
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            return true;
        }

        UserRole role = user.getRole();
        
        // User permissions
        if (permission == Permission.USER_MANAGE) {
            return role == UserRole.SUPER_ADMIN;
        }
        
        // Store permissions
        if (permission == Permission.STORE_MANAGE) {
            return role == UserRole.SUPER_ADMIN || role == UserRole.STORE_MANAGER;
        }
        
        // Product permissions
        if (permission == Permission.PRODUCT_MANAGE) {
            return role == UserRole.STORE_OWNER || role == UserRole.STORE_MANAGER;
        }
        
        // Order permissions
        if (permission == Permission.ORDER_CREATE) {
            return role == UserRole.CUSTOMER;
        }
        if (permission == Permission.ORDER_READ) {
            return true; // All roles can view orders
        }
        
        // Analytics permissions
        if (permission == Permission.ANALYTICS_READ) {
            return role == UserRole.SUPER_ADMIN || role == UserRole.STORE_MANAGER || role == UserRole.STORE_OWNER;
        }
        
        // Review permissions
        if (permission == Permission.REVIEW_MANAGE) {
            return role == UserRole.STORE_OWNER || role == UserRole.STORE_MANAGER;
        }
        
        // Default to false for any other permissions
        return false;
    }

    public void checkPermission(RequirePermission requirePermission) {
        if (!hasPermission(requirePermission.value())) {
            throw new AccessDeniedException("You don't have permission to perform this action");
        }
    }
} 