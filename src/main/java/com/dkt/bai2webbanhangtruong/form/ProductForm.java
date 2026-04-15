package com.dkt.bai2webbanhangtruong.form;

import com.dkt.bai2webbanhangtruong.entity.Product;
import org.springframework.web.multipart.MultipartFile;

public class ProductForm {
    private String code;
    private String name;
    private double price;
    private int stock;
    private boolean newProduct = false;
    private MultipartFile fileData;

    // Constructor cho trường hợp THÊM MỚI
    public ProductForm() {
        this.newProduct = true;
    }

    // Constructor cho trường hợp SỬA: Đổ dữ liệu từ Product sang Form
    public ProductForm(Product product) {
        if (product != null) {
            this.code = product.getCode();
            this.name = product.getName();
            this.price = product.getPrice();
            this.stock = product.getStock();
            this.newProduct = false;
        }
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isNewProduct() { return newProduct; }
    public void setNewProduct(boolean newProduct) { this.newProduct = newProduct; }

    public MultipartFile getFileData() { return fileData; }
    public void setFileData(MultipartFile fileData) { this.fileData = fileData; }
}