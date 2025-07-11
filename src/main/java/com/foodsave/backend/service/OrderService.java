package com.foodsave.backend.service;

import com.foodsave.backend.domain.enums.OrderStatus;
import com.foodsave.backend.domain.enums.PaymentMethod;
import com.foodsave.backend.domain.enums.PaymentStatus;
import com.foodsave.backend.dto.OrderDTO;
import com.foodsave.backend.dto.OrderItemDTO;
import com.foodsave.backend.entity.Order;
import com.foodsave.backend.entity.Store;
import com.foodsave.backend.entity.User;
import com.foodsave.backend.entity.OrderItem;
import com.foodsave.backend.entity.Product;
import com.foodsave.backend.repository.OrderRepository;
import com.foodsave.backend.repository.ProductRepository;
import com.foodsave.backend.exception.ResourceNotFoundException;
import com.foodsave.backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final SecurityUtils securityUtils;

    public List<OrderDTO> getAllOrders() {
        try {
            return orderRepository.findAll().stream()
                    .map(OrderDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all orders: ", e);
            throw new RuntimeException("Failed to fetch orders: " + e.getMessage());
        }
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<OrderDTO> getCurrentUserOrders() {
        User currentUser = securityUtils.getCurrentUser();
        return orderRepository.findByUser(currentUser).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getCurrentStoreOrders() {
        Store currentStore = securityUtils.getCurrentStore();
        return orderRepository.findByStore(currentStore).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateOrderTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating new order: {}", orderDTO);
        
        // Validate store exists
        Store store = productRepository.findById(orderDTO.getItems().get(0).getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + orderDTO.getItems().get(0).getProductId()))
            .getStore();
        
        // Create order
        Order order = new Order();
        order.setStore(store);
        order.setUser(securityUtils.getCurrentUser());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setContactPhone(orderDTO.getContactPhone());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setDeliveryNotes(orderDTO.getDeliveryNotes());
        
        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));
            
            // Check if sufficient stock is available using ProductService
            if (!productService.hasSufficientStock(itemDTO.getProductId(), itemDTO.getQuantity())) {
                throw new IllegalArgumentException("Insufficient stock for product '" + product.getName() + 
                    "'. Available: " + product.getStockQuantity() + ", Requested: " + itemDTO.getQuantity());
            }
            
            // Reduce stock quantity using ProductService
            productService.reduceStockQuantity(itemDTO.getProductId(), itemDTO.getQuantity());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.calculateTotalPrice();
            
            orderItems.add(orderItem);
        }
        
        // Set order items and calculate total
        order.setItems(orderItems);
        order.setTotal(calculateOrderTotal(orderItems));
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder);
        
        return OrderDTO.fromEntity(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        // Update basic order properties
        if (orderDTO.getContactPhone() != null) {
            order.setContactPhone(orderDTO.getContactPhone());
        }
        
        if (orderDTO.getPaymentMethod() != null) {
            order.setPaymentMethod(orderDTO.getPaymentMethod());
        }
        
        if (orderDTO.getStatus() != null) {
            order.setStatus(orderDTO.getStatus());
        }
        
        // Update order items if provided
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            // Clear existing items
            order.getItems().clear();
            order.setSubtotal(BigDecimal.ZERO);
            
            // Validate all products belong to the same store as the order
            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));
                
                if (!product.getStore().getId().equals(order.getStore().getId())) {
                    throw new IllegalArgumentException("All products must belong to the same store as the original order");
                }
                
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setUnitPrice(product.getPrice());
                orderItem.calculateTotalPrice();
                
                order.getItems().add(orderItem);
            }
            
            // Recalculate totals
            order.calculateTotals();
        }
        
        return OrderDTO.fromEntity(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        return OrderDTO.fromEntity(orderRepository.save(order));
    }
}
