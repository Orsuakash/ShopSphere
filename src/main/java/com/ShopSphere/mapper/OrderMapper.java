package com.ShopSphere.mapper;

import com.ShopSphere.DTOS.OrderItemResponse;
import com.ShopSphere.DTOS.OrderResponse;
import com.ShopSphere.Entity.Order;
import com.ShopSphere.Entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        BigDecimal subtotal =
                item.getPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        );

        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(subtotal)
                .build();
    }
}