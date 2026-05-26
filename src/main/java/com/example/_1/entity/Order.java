package com.example._1.entity;

import com.example._1.enums.EnumOrderStatus;
import com.example._1.enums.EnumPayment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderCode; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private EnumOrderStatus status;

    @Column
    private LocalDateTime orderDate;

    @Column
    private BigDecimal totalPrice;

   @Enumerated(EnumType.STRING)
   private EnumPayment paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (status == null) status = EnumOrderStatus.PENDING;
        if (orderCode == null) orderCode = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
