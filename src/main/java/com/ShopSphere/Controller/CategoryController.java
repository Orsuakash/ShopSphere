package com.ShopSphere.Controller;

import com.ShopSphere.DTOS.CategoryResponse;
import com.ShopSphere.DTOS.CreateCategoryRequest;
import com.ShopSphere.DTOS.RegisterRequest;
import com.ShopSphere.DTOS.UpdateCategoryRequest;
import com.ShopSphere.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;




    @PostMapping

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));

    }


@GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(){

        return ResponseEntity.ok(categoryService.getAll());

}
@GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id){

        return ResponseEntity.ok(categoryService.getCategory(id));

    }
@PutMapping("/{id}")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public ResponseEntity<CategoryResponse> updateCategory
        (@PathVariable Long id, @RequestBody @Valid UpdateCategoryRequest request) {
    return ResponseEntity.ok(
            categoryService.updateCategory(id, request)

    );
}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
