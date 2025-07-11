package com.foodsave.backend.service;

import com.foodsave.backend.dto.CartDTO;
import com.foodsave.backend.dto.CartItemDTO;
import com.foodsave.backend.entity.Cart;
import com.foodsave.backend.entity.CartItem;
import com.foodsave.backend.entity.Product;
import com.foodsave.backend.entity.User;
import com.foodsave.backend.exception.ResourceNotFoundException;
import com.foodsave.backend.repository.CartItemRepository;
import com.foodsave.backend.repository.CartRepository;
import com.foodsave.backend.repository.ProductRepository;
import com.foodsave.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(CartDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CartDTO getCartByUser(String email) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
        return CartDTO.fromEntity(cart);
    }

    public CartDTO addItemToCart(String email, CartItemDTO cartItemDTO) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
        
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if product has sufficient stock
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        int requestedQuantity = cartItemDTO.getQuantity();
        int currentCartQuantity = existingItem.map(CartItem::getQuantity).orElse(0);
        int totalQuantity = currentCartQuantity + requestedQuantity;

        if (product.getStockQuantity() < totalQuantity) {
            throw new IllegalArgumentException("Insufficient stock for product '" + product.getName() + 
                "'. Available: " + product.getStockQuantity() + 
                ", In cart: " + currentCartQuantity + 
                ", Requested: " + requestedQuantity);
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(totalQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemDTO.getQuantity());
            cart.getItems().add(cartItemRepository.save(newItem));
        }

        return CartDTO.fromEntity(cartRepository.save(cart));
    }

    public CartDTO updateCartItem(String email, Long itemId, int quantity) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            // Check if product has sufficient stock for the new quantity
            Product product = item.getProduct();
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient stock for product '" + product.getName() + 
                    "'. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
            }
            
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return CartDTO.fromEntity(cartRepository.save(cart));
    }

    public CartDTO removeItemFromCart(String email, Long itemId) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return CartDTO.fromEntity(cartRepository.save(cart));
    }

    public void clearCart(String email) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public int getCartItemCount(String email) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public BigDecimal getCartTotal(String email) {
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}
