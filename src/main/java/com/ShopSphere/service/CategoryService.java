package com.ShopSphere.service;

import com.ShopSphere.DTOS.CategoryResponse;
import com.ShopSphere.DTOS.CreateCategoryRequest;
import com.ShopSphere.DTOS.UpdateCartItemRequest;
import com.ShopSphere.DTOS.UpdateCategoryRequest;
import com.ShopSphere.Entity.Category;
import com.ShopSphere.ExceptionHandling.BadRequestException;
import com.ShopSphere.ExceptionHandling.ResourceNotFoundExpection;
import com.ShopSphere.Repository.CategoryRepository;
import com.ShopSphere.mapper.CategoryMapper;
import org.apache.commons.logging.Log;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

// created
    @Transactional
    public CategoryResponse  createCategory(CreateCategoryRequest categoryRequest){
        if(categoryRepository.existsByName(categoryRequest.getName())){
            throw new BadRequestException("Category already exists");
        }

        Category category= Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();

        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }


    // getmethod
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(){
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse).toList();
    }

    //getByidmethod
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {
        Category category = findCategory(id);
        return categoryMapper.toResponse(category);
    }


    //update
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category=findCategory(id);
        if (!category.getName().equals(request.getName())
                && categoryRepository.existsByName(
                request.getName())) {

            throw  new BadRequestException("catagory already exists");

        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);

        return categoryMapper.toResponse(category);
    }

    //delete

    @Transactional
    public void deleteCategory(Long id){

        Category category= findCategory(id);

        categoryRepository.delete(category);


}

//idmethod
    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Category not found with id: " + id

                        )

                );
    }
    }

