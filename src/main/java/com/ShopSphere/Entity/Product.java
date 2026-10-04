package com.ShopSphere.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length=2000)
    private String description;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal price;

    private  String imageUrl;
    private  Integer stock;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name="category_id"
    )
    private Category category;

    private boolean deleted =false;
}
