package com.dkt.bai2webbanhangtruong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "Orders")
@Getter @Setter @NoArgsConstructor
public class Order extends BaseEntity {
    @Column(name = "Order_Date", nullable = false)
    private Date orderDate;

    @Column(name = "Order_Num", nullable = false)
    private int orderNum;

    @Column(name = "Amount", nullable = false)
    private double amount;

    @Column(name = "User_Name", length = 20)
    private String userName;

    @Column(name = "Status", length = 20, nullable = false)
    private String status;

    private String customerName;
    private String customerAddress;
    private String customerEmail;
    private String customerPhone;
}