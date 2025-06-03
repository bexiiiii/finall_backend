package com.foodsave.backend.service;

import com.foodsave.backend.dto.StoreDTO;
import com.foodsave.backend.dto.UserDTO;
import com.foodsave.backend.entity.Store;
import com.foodsave.backend.entity.User;
import com.foodsave.backend.domain.enums.StoreStatus;
import com.foodsave.backend.exception.ResourceNotFoundException;
import com.foodsave.backend.repository.StoreRepository;
import com.foodsave.backend.repository.UserRepository;
import com.foodsave.backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    public Page<StoreDTO> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable)
                .map(StoreDTO::fromEntity);
    }

    public StoreDTO getStoreById(Long id) {
        return storeRepository.findById(id)
                .map(StoreDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
    }

    public StoreDTO createStore(StoreDTO storeDTO) {
        // Find existing user by email
        User existingUser = userRepository.findByEmail(storeDTO.getUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + storeDTO.getUser().getEmail()));

        // Create store
        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setDescription(storeDTO.getDescription());
        store.setAddress(storeDTO.getAddress());
        store.setPhone(storeDTO.getPhone());
        store.setEmail(storeDTO.getEmail());
        store.setLogo(storeDTO.getLogo());
        store.setOpeningHours(storeDTO.getOpeningHours());
        store.setClosingHours(storeDTO.getClosingHours());
        store.setCategory(storeDTO.getCategory());
        store.setActive(storeDTO.isActive());
        store.setStatus(storeDTO.getStatus());
        store.setOwner(existingUser); // Link with existing user

        Store savedStore = storeRepository.save(store);
        StoreDTO responseDTO = StoreDTO.fromEntity(savedStore);
        responseDTO.setUser(UserDTO.fromEntity(existingUser));
        return responseDTO;
    }

    public StoreDTO createStore(StoreDTO storeDTO, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + ownerEmail));
        Store store = new Store();
        updateStoreFromDTO(store, storeDTO);
        store.setOwner(owner);
        store.setStatus(StoreStatus.PENDING);
        return StoreDTO.fromEntity(storeRepository.save(store));
    }

    public StoreDTO updateStore(Long id, StoreDTO storeDTO) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        store.setName(storeDTO.getName());
        store.setDescription(storeDTO.getDescription());
        store.setAddress(storeDTO.getAddress());
        store.setPhone(storeDTO.getPhone());
        store.setEmail(storeDTO.getEmail());
        store.setLogo(storeDTO.getLogo());
        store.setOpeningHours(storeDTO.getOpeningHours());
        store.setClosingHours(storeDTO.getClosingHours());
        store.setCategory(storeDTO.getCategory());
        store.setActive(storeDTO.isActive());
        store.setStatus(storeDTO.getStatus());

        Store savedStore = storeRepository.save(store);
        StoreDTO responseDTO = StoreDTO.fromEntity(savedStore);
        responseDTO.setUser(UserDTO.fromEntity(store.getOwner()));
        return responseDTO;
    }

    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store not found with id: " + id);
        }
        storeRepository.deleteById(id);
    }

    public StoreDTO updateStoreStatus(Long id, StoreStatus status) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        store.setStatus(status);
        return StoreDTO.fromEntity(storeRepository.save(store));
    }

    public Page<StoreDTO> searchStores(String query, Pageable pageable) {
        return storeRepository.searchStores(query, pageable)
                .map(StoreDTO::fromEntity);
    }

    public Page<StoreDTO> findNearbyStores(double latitude, double longitude, double radius, Pageable pageable) {
        // Convert radius from kilometers to degrees (approximate)
        double latDelta = radius / 111.0;
        double lngDelta = radius / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        return storeRepository.findStoresInArea(
                latitude - latDelta,
                latitude + latDelta,
                longitude - lngDelta,
                longitude + lngDelta,
                pageable
        ).map(StoreDTO::fromEntity);
    }

    public Page<StoreDTO> getStoresByLocation(String location, Pageable pageable) {
        return storeRepository.findByAddressContaining(location, pageable)
                .map(StoreDTO::fromEntity);
    }

    public Page<StoreDTO> getStoresByOwner(String ownerEmail, Pageable pageable) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + ownerEmail));
        return storeRepository.findByOwner(owner, pageable)
                .map(StoreDTO::fromEntity);
    }

    public StoreDTO getCurrentUserStore() {
        User currentUser = securityUtils.getCurrentUser();
        return storeRepository.findByOwner(currentUser)
                .map(StoreDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found for current user"));
    }

    private void updateStoreFromDTO(Store store, StoreDTO dto) {
        store.setName(dto.getName());
        store.setDescription(dto.getDescription());
        store.setAddress(dto.getAddress());
        store.setPhone(dto.getPhone());
        store.setEmail(dto.getEmail());
        store.setLogo(dto.getLogo());
        store.setOpeningHours(dto.getOpeningHours());
        store.setClosingHours(dto.getClosingHours());
        store.setCategory(dto.getCategory());
        store.setActive(dto.isActive());
        store.setStatus(dto.getStatus());
    }
}
