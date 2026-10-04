package com.ShopSphere.Controller;

import com.ShopSphere.DTOS.CreateOrderRequest;
import com.ShopSphere.DTOS.OrderResponse;
import com.ShopSphere.DTOS.UpdateOrderStatusRequest;
import com.ShopSphere.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    // CREATE ORDER
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }


    // GET MY ORDERS
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(
                orderService.getMyOrders()
        );
    }


    // GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }


    // CANCEL ORDER
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancelOrder(id)
        );
    }


    // ADMIN UPDATE STATUS
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        request
                )
        );
    }
}