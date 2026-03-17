package com.dkt.bai2webbanhangtruong.repository;

import com.dkt.bai2webbanhangtruong.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {

    @Override
    Page<T> findAll(Pageable pageable);

    @Query(value = "SELECT t.* FROM reservation t where t.party_size = :size", nativeQuery = true)
    List<T> findByParty(@Param("size") int size);

}