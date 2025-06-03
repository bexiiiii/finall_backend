package com.foodsave.backend.controller;

import com.foodsave.backend.domain.enums.OrderStatus;
import com.foodsave.backend.domain.enums.Permission;
import com.foodsave.backend.dto.OrderDTO;
import com.foodsave.backend.security.annotation.RequirePermission;
import com.foodsave.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @RequirePermission(Permission.ORDER_READ)
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @RequirePermission(Permission.ORDER_READ)
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/my-orders")
    @RequirePermission(Permission.ORDER_READ)
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        return ResponseEntity.ok(orderService.getCurrentUserOrders());
    }

    @GetMapping("/store-orders")
    @RequirePermission(Permission.ORDER_READ)
    public ResponseEntity<List<OrderDTO>> getStoreOrders() {
        return ResponseEntity.ok(orderService.getCurrentStoreOrders());
    }

    @PostMapping
    @RequirePermission(Permission.ORDER_CREATE)
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @PutMapping("/{id}")
    @RequirePermission(Permission.ORDER_UPDATE)
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(Permission.ORDER_DELETE)
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @RequirePermission(Permission.ORDER_UPDATE)
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
