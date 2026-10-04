package com.shopsphere.service;

import com.ShopSphere.DTOS.CreateProductRequest;
import com.ShopSphere.DTOS.ProductResponse;
import com.ShopSphere.DTOS.UpdateProductRequest;
import com.ShopSphere.Entity.Category;
import com.ShopSphere.Entity.Product;
import com.ShopSphere.ExceptionHandling.ResourceNotFoundExpection;
import com.ShopSphere.Repository.CategoryRepository;
import com.ShopSphere.Repository.ProductRepository;
import com.ShopSphere.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(
             CreateProductRequest request) {

        Category category =
                findCategory(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .deleted(false)
                .build();

        Product savedProduct =
                productRepository.save(product);

        return productMapper.toResponce(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );

        return productRepository
                .findByDeletedFalse(pageable)
                .map(productMapper::toResponce);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {

        Product product =
                findProduct(id);

        return productMapper.toResponce(product);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long id,
            UpdateProductRequest request) {

        Product product =
                findProduct(id);

        Category category =
                findCategory(request.getCategoryId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct =
                productRepository.save(product);

        return productMapper.toResponce(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {

        Product product =
                findProduct(id);

        product.setDeleted(true);

        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            String keyword,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("name").ascending()
                );

        return productRepository
                .findByNameContainingIgnoreCaseAndDeletedFalse(
                        keyword,
                        pageable
                )
                .map(productMapper::toResponce);
    }

    private Product findProduct(Long id) {

        return productRepository
                .findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Product not found with id: " + id
                        )
                );
    }

    private Category findCategory(Long id) {

        return categoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Category not found with id: " + id
                        )
                );
    }
}