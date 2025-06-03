package com.foodsave.backend.controller;

import com.foodsave.backend.entity.Store;
import com.foodsave.backend.dto.StoreDTO;
import com.foodsave.backend.service.StoreService;
import com.foodsave.backend.domain.enums.StoreStatus;
import com.foodsave.backend.security.annotation.RequirePermission;
import com.foodsave.backend.domain.enums.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store Management", description = "APIs for managing stores")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @Operation(summary = "Get all stores")
    @RequirePermission(Permission.STORE_READ)
    public ResponseEntity<Page<StoreDTO>> getAllStores(Pageable pageable) {
        return ResponseEntity.ok(storeService.getAllStores(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store by ID")
    @RequirePermission(Permission.STORE_READ)
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    // Объединённый метод для удаления магазина
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Delete store", description = "Delete a store (Admins or store owner only)")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id, Authentication authentication) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update store status")
    @RequirePermission(Permission.STORE_UPDATE)
    public ResponseEntity<StoreDTO> updateStoreStatus(@PathVariable Long id, @RequestParam StoreStatus status) {
        return ResponseEntity.ok(storeService.updateStoreStatus(id, status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search stores")
    @RequirePermission(Permission.STORE_READ)
    public ResponseEntity<Page<StoreDTO>> searchStores(@RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(storeService.searchStores(query, pageable));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby stores")
    @RequirePermission(Permission.STORE_READ)
    public ResponseEntity<Page<StoreDTO>> findNearbyStores(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius,
            Pageable pageable) {
        return ResponseEntity.ok(storeService.findNearbyStores(latitude, longitude, radius, pageable));
    }

    @GetMapping("/by-location")
    @Operation(summary = "Get stores by location", description = "Get stores in a specific location")
    public ResponseEntity<Page<StoreDTO>> getStoresByLocation(
            @Parameter(description = "Location to search in")
            @RequestParam String location,
            Pageable pageable) {
        return ResponseEntity.ok(storeService.getStoresByLocation(location, pageable));
    }

    @PostMapping
    @RequirePermission(Permission.STORE_CREATE)
    @Operation(summary = "Create new store", description = "Create a new store (Store owners and admins only)")
    public ResponseEntity<StoreDTO> createStore(
            @Valid @RequestBody StoreDTO storeDTO,
            Authentication authentication) {
        StoreDTO createdStore = storeService.createStore(storeDTO, authentication.getName());
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @RequirePermission(Permission.STORE_UPDATE)
    @Operation(summary = "Update store", description = "Update store information (Admins or store owner only)")
    public ResponseEntity<StoreDTO> updateStore(
            @Parameter(description = "Store ID") @PathVariable Long id,
            @Valid @RequestBody StoreDTO storeDTO,
            Authentication authentication) {
        StoreDTO updatedStore = storeService.updateStore(id, storeDTO);
        return ResponseEntity.ok(updatedStore);
    }

    @GetMapping("/my-stores")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get my stores", description = "Get stores owned by the current user")
    public ResponseEntity<Page<StoreDTO>> getMyStores(
            Authentication authentication,
            Pageable pageable) {
        return ResponseEntity.ok(storeService.getStoresByOwner(authentication.getName(), pageable));
    }

    @GetMapping("/my-store")
    @RequirePermission(Permission.STORE_READ)
    public ResponseEntity<StoreDTO> getMyStore() {
        return ResponseEntity.ok(storeService.getCurrentUserStore());
    }
}
