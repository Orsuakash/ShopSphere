package com.ShopSphere.DTOS;

import com.ShopSphere.Entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter

@Setter

public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")

    private OrderStatus status;

}
