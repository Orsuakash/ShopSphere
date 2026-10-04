package com.ShopSphere.DTOS;

import com.ShopSphere.Entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter

@Builder

public class OrderResponse {

    private Long id;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private String shippingAddress;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

}
