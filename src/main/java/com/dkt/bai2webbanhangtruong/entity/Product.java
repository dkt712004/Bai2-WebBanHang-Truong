package com.dkt.bai2webbanhangtruong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Products")
@Getter @Setter @NoArgsConstructor
public class Product extends BaseEntity {
    @Column(name = "Code", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "Name", length = 255, nullable = false)
    private String name;

    @Column(name = "Price", nullable = false)
    private double price;

    @Lob
    @Column(name = "Image", columnDefinition = "LONGBLOB")
    private byte[] image;
}