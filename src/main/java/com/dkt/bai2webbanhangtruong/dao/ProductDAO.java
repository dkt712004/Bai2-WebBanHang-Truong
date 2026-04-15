package com.dkt.bai2webbanhangtruong.dao;

import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.form.ProductForm;
import com.dkt.bai2webbanhangtruong.model.ProductInfo;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import com.dkt.bai2webbanhangtruong.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;

@Repository
@Transactional
public class ProductDAO {

    private final ProductRepository productRepository;

    public ProductDAO(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findProduct(String code) {
        return productRepository.findByCode(code);
    }

    public void save(ProductForm productForm) {
        String code = productForm.getCode();
        Product product = null;

        if (!productForm.isNewProduct()) {
            product = this.findProduct(code);
        }
        if (product == null) {
            product = new Product();
            product.setCode(code);
            product.setCreatedAt(LocalDateTime.now());
        }
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());
        product.setStock(productForm.getStock());

        if (productForm.getFileData() != null && !productForm.getFileData().isEmpty()) {
            try {
                // Lấy dữ liệu ảnh dạng byte[] và gán vào Product
                byte[] imageBytes = productForm.getFileData().getBytes();
                if (imageBytes != null && imageBytes.length > 0) {
                    product.setImage(imageBytes);
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi đọc file ảnh: " + e.getMessage());
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
        Page<ProductInfo> infoPage = resultPage.map(p -> new ProductInfo(p.getCode(), p.getName(), p.getPrice(), p.getStock()));
        return new PaginationResult<>(infoPage, maxNavigationPage);
    }


    public boolean softDelete(String code) {

        Product product = productRepository.findByCode(code.trim());

        if (product != null) {

            product.setDeleted(true);
            product.setDeletedAt(java.time.LocalDateTime.now());


            productRepository.save(product);
            return true;
        }
        return false;
    }
}