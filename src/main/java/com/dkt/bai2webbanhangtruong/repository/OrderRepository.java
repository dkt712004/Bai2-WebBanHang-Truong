package com.dkt.bai2webbanhangtruong.repository;

import com.dkt.bai2webbanhangtruong.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {


    Order findByOrderNum(int orderNum);


}