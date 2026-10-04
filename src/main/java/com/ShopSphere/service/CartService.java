package com.ShopSphere.service;

import com.ShopSphere.DTOS.AddToCartRequest;
import com.ShopSphere.DTOS.CartItemResponse;
import com.ShopSphere.DTOS.CartResponse;
import com.ShopSphere.DTOS.UpdateCartItemRequest;
import com.ShopSphere.Entity.Cart;
import com.ShopSphere.Entity.CartItem;
import com.ShopSphere.Entity.Product;
import com.ShopSphere.Entity.User;
import com.ShopSphere.ExceptionHandling.ResourceNotFoundExpection;
import com.ShopSphere.Repository.CartItemRepository;
import com.ShopSphere.Repository.CartRepository;
import com.ShopSphere.Repository.ProductRepository;
import com.ShopSphere.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    // ADD PRODUCT TO CART
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {

        User user = getCurrentUser();

        Product product = productRepository.findById(
                        request.getProductId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Product not found with id: "
                                        + request.getProductId()
                        )
                );

        if (product.isDeleted()) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Product is not available"
            );
        }

        Cart cart = getOrCreateCart(user);

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                product.getId()
                        )
                        .orElse(null);

        if (cartItem == null) {

            validateStock(
                    product,
                    request.getQuantity()
            );

            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

            cart.getItems().add(cartItem);

        } else {

            int newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            validateStock(product, newQuantity);

            cartItem.setQuantity(newQuantity);
        }

        cartRepository.save(cart);

        return buildCartResponse(cart);
    }


    // GET CART
    @Transactional
    public CartResponse getCart() {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseGet(() -> createEmptyCart(user));

        return buildCartResponse(cart);
    }


    // UPDATE CART ITEM
    @Transactional
    public CartResponse updateCartItem(
            Long productId,
            UpdateCartItemRequest request) {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Cart not found"
                        )
                );

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundExpection(
                                        "Product not found in cart"
                                )
                        );

        Product product = cartItem.getProduct();

        if (product.isDeleted()) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Product is no longer available"
            );
        }

        validateStock(
                product,
                request.getQuantity()
        );

        cartItem.setQuantity(
                request.getQuantity()
        );

        return buildCartResponse(cart);
    }


    // REMOVE ITEM FROM CART
    @Transactional
    public CartResponse removeFromCart(
            Long productId) {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Cart not found"
                        )
                );

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundExpection(
                                        "Product not found in cart"
                                )
                        );

        cart.getItems().remove(cartItem);

        cartItemRepository.delete(cartItem);

        return buildCartResponse(cart);
    }


    // CLEAR CART
    @Transactional
    public void clearCart() {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "Cart not found"
                        )
                );

        cartItemRepository.deleteAll(
                cart.getItems()
        );

        cart.getItems().clear();
    }


    // GET OR CREATE CART
    private Cart getOrCreateCart(User user) {

        return cartRepository
                .findByUserId(user.getId())
                .orElseGet(
                        () -> createEmptyCart(user)
                );
    }


    // CREATE EMPTY CART
    private Cart createEmptyCart(User user) {

        Cart cart = Cart.builder()
                .user(user)
                .build();

        return cartRepository.save(cart);
    }


    // VALIDATE STOCK
    private void validateStock(
            Product product,
            int quantity) {

        if (quantity <= 0) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Quantity must be greater than zero"
            );
        }

        if (quantity > product.getStock()) {
            throw new com.ShopSphere.ExceptionHandling.BadRequestException(
                    "Insufficient stock. Available stock: "
                            + product.getStock()
            );
        }
    }


    // CURRENT USER
    private User getCurrentUser() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundExpection(
                                "User not found"
                        )
                );
    }


    // BUILD CART RESPONSE
    private CartResponse buildCartResponse(
            Cart cart) {

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(this::mapCartItem)
                        .toList();

        BigDecimal totalAmount =
                items.stream()
                        .map(
                                CartItemResponse::getSubtotal
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }


    // MAP CART ITEM
    private CartItemResponse mapCartItem(
            CartItem cartItem) {

        BigDecimal subtotal =
                cartItem.getProduct()
                        .getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(
                        cartItem.getProduct().getId()
                )
                .productName(
                        cartItem.getProduct().getName()
                )
                .price(
                        cartItem.getProduct().getPrice()
                )
                .quantity(
                        cartItem.getQuantity()
                )
                .subtotal(subtotal)
                .build();
    }
}