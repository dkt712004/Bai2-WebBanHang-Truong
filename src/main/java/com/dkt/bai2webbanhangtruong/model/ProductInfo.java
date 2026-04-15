package com.dkt.bai2webbanhangtruong.model;

import com.dkt.bai2webbanhangtruong.entity.Product;

public class ProductInfo {
    private String code;
    private String name;
    private double price;
    private int stock;


    public ProductInfo() {
    }



    public ProductInfo(Product product) {
        if (product != null) {
            this.code = product.getCode();
            this.name = product.getName();
            this.price = product.getPrice();
            this.stock = product.getStock();
        }
    }


    public ProductInfo(String code, String name, double price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}