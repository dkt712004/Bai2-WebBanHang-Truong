package com.dkt.bai2webbanhangtruong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends com.dkt.bai2webbanhangtruong.entity.BaseEntity {

    @Column(name = "User_Name", length = 20, nullable = false, unique = true)
    private String userName;

    @Column(name = "Encryted_Password", length = 128, nullable = false)
    private String encrytedPassword;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @Column(name = "User_Role", length = 20, nullable = false)
    private String userRole;

    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
}