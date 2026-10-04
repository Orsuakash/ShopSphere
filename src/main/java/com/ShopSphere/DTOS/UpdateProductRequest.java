package com.ShopSphere.DTOS;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class UpdateProductRequest {

    @NotBlank(message = "Product name is required")

    private String name;

    private String description;

    @NotNull(message = "Price is required")

    @Positive(message = "Price must be greater than zero")

    private BigDecimal price;

    @NotNull(message = "Stock is required")

    @Min(value = 0, message = "Stock cannot be negative")

    private Integer stock;

    private String imageUrl;

    @NotNull(message = "Category ID is required")

    private Long categoryId;
}
