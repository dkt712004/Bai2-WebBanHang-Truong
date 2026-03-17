package com.dkt.bai2webbanhangtruong.repository;

import com.dkt.bai2webbanhangtruong.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {

    Product findByCode(String code);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}