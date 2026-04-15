package com.dkt.bai2webbanhangtruong.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Products")
@Getter @Setter @NoArgsConstructor
@SQLRestriction("is_deleted = 0")
public class Product extends BaseEntity {
    @Column(name = "Code", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "Name", length = 255, nullable = false)
    private String name;

    @Column(name = "Price", nullable = false)
    private double price;

    @Column(name = "Stock", nullable = false)
    private int stock;

    @Lob
    @Column(name = "Image", columnDefinition = "LONGBLOB")
    private byte[] image;
}