package com.ShopSphere.DTOS;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")

    private String name;

    @Size(max = 2000, message = "Description is too long")

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
