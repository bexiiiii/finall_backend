package com.foodsave.backend.controller;

import com.foodsave.backend.domain.enums.Permission;
import com.foodsave.backend.domain.enums.UserRole;
import com.foodsave.backend.security.PermissionHandler;
import com.foodsave.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for managing permissions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PermissionController {

    private final PermissionHandler permissionHandler;
    private final UserService userService;

    @GetMapping("/check")
    @Operation(summary = "Check if current user has permission")
    public ResponseEntity<Boolean> checkPermission(@RequestParam Permission permission) {
        return ResponseEntity.ok(permissionHandler.hasPermission(permission));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user permissions")
    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable String userId) {
        try {
            var user = userService.getUserById(Long.parseLong(userId));
            if (user.getRole() == UserRole.SUPER_ADMIN) {
                return ResponseEntity.ok(Arrays.asList(Permission.values()));
            }
            // TODO: Implement actual permission retrieval from database
            return ResponseEntity.ok(List.of(
                Permission.USER_READ,
                Permission.USER_CREATE,
                Permission.USER_UPDATE,
                Permission.USER_DELETE,
                Permission.ROLE_READ,
                Permission.ROLE_CREATE,
                Permission.ROLE_UPDATE,
                Permission.ROLE_DELETE,
                Permission.STORE_READ,
                Permission.STORE_CREATE,
                Permission.STORE_UPDATE,
                Permission.STORE_DELETE,
                Permission.PRODUCT_READ,
                Permission.PRODUCT_CREATE,
                Permission.PRODUCT_UPDATE,
                Permission.PRODUCT_DELETE,
                Permission.ORDER_READ,
                Permission.ORDER_CREATE,
                Permission.ORDER_UPDATE,
                Permission.ORDER_DELETE,
                Permission.ANALYTICS_READ,
                Permission.ANALYTICS_EXPORT,
                Permission.DISCOUNT_READ,
                Permission.DISCOUNT_CREATE,
                Permission.DISCOUNT_UPDATE,
                Permission.DISCOUNT_DELETE,
                Permission.REVIEW_READ,
                Permission.REVIEW_CREATE,
                Permission.REVIEW_UPDATE,
                Permission.REVIEW_DELETE,
                Permission.CATEGORY_READ,
                Permission.CATEGORY_CREATE,
                Permission.CATEGORY_UPDATE,
                Permission.CATEGORY_DELETE,
                Permission.SETTINGS_READ,
                Permission.SETTINGS_UPDATE,
                Permission.NOTIFICATION_READ,
                Permission.NOTIFICATION_CREATE,
                Permission.NOTIFICATION_UPDATE,
                Permission.NOTIFICATION_DELETE
            ));
        } catch (Exception e) {
            // Если пользователь не найден или другая ошибка, возвращаем базовые разрешения
            return ResponseEntity.ok(List.of(
                Permission.USER_READ,
                Permission.PRODUCT_READ,
                Permission.STORE_READ,
                Permission.ORDER_READ
            ));
        }
    }

    @PostMapping("/roles/{roleName}/permissions")
    @Operation(summary = "Assign permissions to role")
    public ResponseEntity<Void> assignPermissionsToRole(
            @PathVariable String roleName,
            @RequestBody List<Permission> permissions) {
        // TODO: Implement actual permission assignment
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/roles/{roleName}/permissions")
    @Operation(summary = "Remove permissions from role")
    public ResponseEntity<Void> removePermissionsFromRole(
            @PathVariable String roleName,
            @RequestBody List<Permission> permissions) {
        // TODO: Implement actual permission removal
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable String userId,
            @RequestParam String newRole) {
        // TODO: Implement actual role update
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/permissions")
    @Operation(summary = "Update user permissions")
    public ResponseEntity<Void> updateUserPermissions(
            @PathVariable String userId,
            @RequestBody List<Permission> permissions) {
        // TODO: Implement actual permission update
        return ResponseEntity.ok().build();
    }
} 