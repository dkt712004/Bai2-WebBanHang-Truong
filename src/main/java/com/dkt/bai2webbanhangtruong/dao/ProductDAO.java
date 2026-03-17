package com.dkt.bai2webbanhangtruong.dao;

import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.form.ProductForm;
import com.dkt.bai2webbanhangtruong.model.ProductInfo;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import com.dkt.bai2webbanhangtruong.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

@Repository
@Transactional
public class ProductDAO {

    @Autowired
    private ProductRepository productRepository;

    public Product findProduct(String code) {
        return productRepository.findByCode(code);
    }

    public void save(ProductForm productForm) {
        String code = productForm.getCode();
        Product product = (code != null) ? productRepository.findByCode(code) : null;

        if (product == null) {
            product = new Product();
            product.setCode(code);
        }
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());

        if (productForm.getFileData() != null && !productForm.getFileData().isEmpty()) {
            try {
                product.setImage(productForm.getFileData().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productRepository.save(product);
    }

    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage, String likeName) {
        Pageable pageable = PageRequest.of(page - 1, maxResult, Sort.by("id").descending());
        Page<Product> resultPage;

        if (likeName != null && !likeName.trim().isEmpty()) {
            resultPage = productRepository.findByNameContainingIgnoreCase(likeName, pageable);
        } else {
            resultPage = productRepository.findAll(pageable);
        }

        // Chuyển đổi sang ProductInfo để hiển thị ra View
        Page<ProductInfo> infoPage = resultPage.map(p -> new ProductInfo(p.getCode(), p.getName(), p.getPrice()));
        return new PaginationResult<>(infoPage, maxNavigationPage);
    }
}