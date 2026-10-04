package com.ShopSphere.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class CartItem {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "cart_id",
nullable = false)
private Cart cart;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name="product_id",
nullable = false)
private Product product;

@Column(nullable = false)
private Integer quantity;

}
