package com.ShopSphere.Controller;

import com.ShopSphere.DTOS.AddToCartRequest;
import com.ShopSphere.DTOS.CartResponse;
import com.ShopSphere.DTOS.UpdateCartItemRequest;
import com.ShopSphere.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    // ADD PRODUCT TO CART
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(request)
        );
    }


    // GET CART
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        return ResponseEntity.ok(
                cartService.getCart()
        );
    }


    // UPDATE CART ITEM QUANTITY
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(
                cartService.updateCartItem(
                        productId,
                        request
                )
        );
    }


    // REMOVE PRODUCT FROM CART
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                cartService.removeFromCart(productId)
        );
    }


    // CLEAR CART
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {

        cartService.clearCart();

        return ResponseEntity.noContent().build();
    }
}