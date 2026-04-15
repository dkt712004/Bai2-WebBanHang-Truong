package com.dkt.bai2webbanhangtruong.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ProductService {

    @PersistenceContext
    private EntityManager entityManager; // Công cụ điều khiển DB trực tiếp

    @Transactional
    public boolean softDeleteByCode(String code) {

        String sql = "UPDATE products SET is_deleted = 1, deleted_at = ?1 WHERE code = ?2";

        int updatedCount = entityManager.createNativeQuery(sql)
                .setParameter(1, LocalDateTime.now())
                .setParameter(2, code.trim())
                .executeUpdate();

        return updatedCount > 0;
    }
}