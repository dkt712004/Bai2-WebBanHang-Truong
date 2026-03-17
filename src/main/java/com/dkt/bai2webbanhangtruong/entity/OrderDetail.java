package com.dkt.bai2webbanhangtruong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Order_Details")
@Getter @Setter @NoArgsConstructor
public class OrderDetail extends BaseEntity {

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "Quanity", nullable = false)
    private int quanity;

    @Column(name = "Price", nullable = false)
    private double price;

    @Column(name = "Amount", nullable = false)
    private double amount;
}