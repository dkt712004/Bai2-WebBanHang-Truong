package com.dkt.bai2webbanhangtruong.controller;

import com.dkt.bai2webbanhangtruong.dao.OrderDAO;
import com.dkt.bai2webbanhangtruong.model.OrderDetailInfo;
import com.dkt.bai2webbanhangtruong.model.OrderInfo;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderDAO orderDAO;

    public OrderApiController(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }


    @GetMapping
    public ResponseEntity<PaginationResult<OrderInfo>> getOrderList(
            @RequestParam(value = "page", defaultValue = "1") int page) {

        PaginationResult<OrderInfo> result = orderDAO.listOrderInfo(page, 10, 5);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderInfo> getOrderDetail(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderDAO.getOrderInfo(orderId);
        if (orderInfo != null) {
            // Join thủ công lấy danh sách sản phẩm trong đơn đó
            List<OrderDetailInfo> details = orderDAO.listOrderDetailInfos(orderId);
            orderInfo.setDetails(details);
            return ResponseEntity.ok(orderInfo);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        orderDAO.updateStatus(orderId, status);
        return ResponseEntity.ok("{\"message\": \"Thành công\"}");
    }
}