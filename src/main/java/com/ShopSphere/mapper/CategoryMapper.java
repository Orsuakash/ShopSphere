package com.ShopSphere.mapper;

import com.ShopSphere.DTOS.CategoryResponse;
import com.ShopSphere.Entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
