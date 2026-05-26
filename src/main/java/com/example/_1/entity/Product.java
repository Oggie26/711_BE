package com.example._1.entity;

import com.example._1.enums.EnumStatus;
import com.example._1.enums.EnumUnit;
import com.example._1.util.SlugUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String description;

    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    private EnumUnit unit;

    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @Column
    private String thumbnail;

    @Column
    private Integer stock = 0;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    public void prePersist() {
        if (this.barcode == null || this.barcode.isBlank()) {
            this.barcode = generateBarcode();
        }
    }

    private String generateBarcode() {
        return "PRD-" + UUID.randomUUID().toString()
                .substring(0, 8)
                .toUpperCase();
    }
}