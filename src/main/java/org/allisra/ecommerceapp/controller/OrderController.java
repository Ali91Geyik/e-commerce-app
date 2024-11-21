package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.order.CreateOrderDTO;
import org.allisra.ecommerceapp.model.dto.order.OrderDTO;
import org.allisra.ecommerceapp.model.dto.order.UpdateOrderStatusDTO;
import org.allisra.ecommerceapp.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO createDTO) {
        OrderDTO createdOrder = orderService.createOrder(createDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<OrderDTO>> getUserOrdersByStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        return ResponseEntity.ok(orderService.getUserOrdersByStatus(userId, status));
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Valid @RequestBody UpdateOrderStatusDTO updateDTO) {
        return ResponseEntity.ok(orderService.updateOrderStatus(updateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}