package com.dkt.bai2webbanhangtruong.repository;

import com.dkt.bai2webbanhangtruong.entity.OrderDetail;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends BaseRepository<OrderDetail, Long> {

    // Thêm dòng này để Spring Data JPA tự động tạo query:
    // SELECT * FROM Order_Details WHERE ORDER_ID = ?
    List<OrderDetail> findByOrderId(Long orderId);

}