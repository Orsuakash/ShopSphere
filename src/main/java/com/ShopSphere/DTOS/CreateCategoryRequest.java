package com.ShopSphere.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {


    @NotBlank(message = "Category name is required")

    private String name;

    @Size(max = 500, message = "Description is too long")

    private String description;
}
