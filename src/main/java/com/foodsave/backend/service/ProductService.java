package com.foodsave.backend.service;

import com.foodsave.backend.entity.Product;
import com.foodsave.backend.entity.Store;
import com.foodsave.backend.entity.Category;
import com.foodsave.backend.dto.ProductDTO;
import com.foodsave.backend.repository.ProductRepository;
import com.foodsave.backend.repository.StoreRepository;
import com.foodsave.backend.repository.CategoryRepository;
import com.foodsave.backend.domain.enums.ProductStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return convertToDTO(product);
    }

    public Page<ProductDTO> getProductsByStore(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
        return productRepository.findByStore(store, pageable)
                .map(this::convertToDTO);
    }

    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    public Page<ProductDTO> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByDiscountPercentageGreaterThan(0.0, pageable)
                .map(this::convertToDTO);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Store store = storeRepository.findById(productDTO.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = new Product();
        updateProductFromDTO(product, productDTO);
        product.setStore(store);
        product.setCategory(category);
        return convertToDTO(productRepository.save(product));
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        
        updateProductFromDTO(product, productDTO);
        product.setCategory(category);
        return convertToDTO(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return productRepository.findByCategory(category, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getDiscountedProducts(Pageable pageable) {
        return productRepository.findByDiscountPercentageGreaterThan(0.0, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getLowStockProducts(Integer threshold, Pageable pageable) {
        return productRepository.findByStockQuantityLessThanEqual(threshold, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getExpiringProducts(Pageable pageable) {
        return productRepository.findByExpiryDateIsNotNull(pageable)
                .map(this::convertToDTO);
    }

    public ProductDTO updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setStatus(status);
        return convertToDTO(productRepository.save(product));
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountPercentage(product.getDiscountPercentage())
                .stockQuantity(product.getStockQuantity())
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getName())
                .storeLogo(product.getStore().getLogo())
                .storeAddress(product.getStore().getAddress())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .images(product.getImages())
                .expiryDate(product.getExpiryDate())
                .status(product.getStatus())
                .active(product.getActive())
                .build();
    }

    private void updateProductFromDTO(Product product, ProductDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setDiscountPercentage(dto.getDiscountPercentage());
        product.setStockQuantity(dto.getStockQuantity());
        product.setImages(dto.getImages());
        product.setExpiryDate(dto.getExpiryDate());
        product.setStatus(dto.getStatus());
        product.setActive(dto.getActive());
    }
}
