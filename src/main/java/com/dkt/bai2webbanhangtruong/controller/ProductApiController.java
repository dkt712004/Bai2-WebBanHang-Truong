package com.dkt.bai2webbanhangtruong.controller;

import com.dkt.bai2webbanhangtruong.dao.ProductDAO;
import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.form.ProductForm;
import com.dkt.bai2webbanhangtruong.model.ProductInfo;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import com.dkt.bai2webbanhangtruong.repository.ProductRepository;
import com.dkt.bai2webbanhangtruong.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductDAO productDAO;
    private final ProductRepository productRepository;
    private final ProductService productService;


    public ProductApiController(ProductDAO productDAO,
                                ProductRepository productRepository,
                                ProductService productService) {
        this.productDAO = productDAO;
        this.productRepository = productRepository;
        this.productService = productService;
    }


    @GetMapping
    public ResponseEntity<PaginationResult<ProductInfo>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String name) {

        return ResponseEntity.ok(productDAO.queryProducts(page, size, 10, name));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProductInfo> detail(@PathVariable String code) {
        Product product = productDAO.findProduct(code);
        if (product != null) {
            return ResponseEntity.ok(new ProductInfo(product));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> save(@ModelAttribute ProductForm productForm) {
        try {
            productDAO.save(productForm);
            return ResponseEntity.ok("{\"message\": \"Lưu sản phẩm thành công!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Lỗi: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteProduct(@PathVariable("code") String code) {
        try {
            // Gọi hàm xóa vừa viết ở DAO
            boolean success = productDAO.softDelete(code);

            if (success) {
                return ResponseEntity.ok("{\"message\": \"Xóa mềm thành công!\"}");
            } else {
                return ResponseEntity.status(404).body("{\"message\": \"Không tìm thấy sản phẩm\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"message\": \"Lỗi Server: " + e.getMessage() + "\"}");
        }
    }
}