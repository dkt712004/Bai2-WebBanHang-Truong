package com.dkt.bai2webbanhangtruong.repository;

import com.dkt.bai2webbanhangtruong.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {

    Product findByCode(String code);

    @Transactional
    @Modifying
    @Query(value = "UPDATE products SET is_deleted = 1, deleted_at = NOW() WHERE code = :code", nativeQuery = true)
    int forceSoftDelete(@Param("code") String code);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}