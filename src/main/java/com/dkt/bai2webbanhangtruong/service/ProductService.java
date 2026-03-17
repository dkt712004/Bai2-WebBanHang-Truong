package com.dkt.bai2webbanhangtruong.service;

import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void save(Product entity) {
        productRepository.save(entity);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findByCode(String code) {
        return productRepository.findByCode(code);
    }
}