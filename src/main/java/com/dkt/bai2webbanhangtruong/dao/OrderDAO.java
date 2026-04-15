package com.dkt.bai2webbanhangtruong.dao;

import com.dkt.bai2webbanhangtruong.entity.Order;
import com.dkt.bai2webbanhangtruong.entity.OrderDetail;
import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.model.*;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import com.dkt.bai2webbanhangtruong.repository.OrderDetailRepository;
import com.dkt.bai2webbanhangtruong.repository.OrderRepository;
import com.dkt.bai2webbanhangtruong.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class OrderDAO {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public OrderDAO(OrderRepository orderRepository,
                    OrderDetailRepository orderDetailRepository,
                    ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
    }

    public void saveOrder(CartInfo cartInfo, String userName) {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
        order.setUserName(userName);
        order.setStatus("NEW");

        CustomerInfo cust = cartInfo.getCustomerInfo();
        if (cust != null && cust.getName() != null && !cust.getName().isEmpty()) {
            order.setCustomerName(cust.getName());
            order.setCustomerEmail(cust.getEmail());
            order.setCustomerPhone(cust.getPhone());
            order.setCustomerAddress(cust.getAddress());
        } else {

            order.setCustomerName(userName);
            order.setCustomerEmail("Chưa cập nhật");
            order.setCustomerAddress("Tại quầy/Hệ thống");
            order.setCustomerPhone("N/A");
        }


        order = orderRepository.save(order);
        order.setOrderNum(order.getId().intValue());
        orderRepository.save(order);

        for (CartLineInfo line : cartInfo.getCartLines()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());


            Product product = productRepository.findByCode(line.getProductInfo().getCode());
            if (product != null) {

                int newStock = product.getStock() - line.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " đã hết hàng!");
                }
                product.setStock(newStock);
                productRepository.save(product); // Cập nhật kho

                detail.setProductId(product.getId());
            }

            detail.setQuanity(line.getQuantity());
            detail.setPrice(line.getProductInfo().getPrice());
            detail.setAmount(line.getAmount());

            orderDetailRepository.save(detail);
        }
        cartInfo.setOrderNum(order.getOrderNum());
    }


    public OrderInfo getOrderInfo(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

        return new OrderInfo(
                order.getId().toString(),
                order.getOrderDate(),
                order.getOrderNum(),
                order.getAmount(),
                order.getCustomerName(),
                order.getCustomerAddress(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getStatus()
        );
    }


    public List<OrderDetailInfo> listOrderDetailInfos(Long orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);

        return details.stream().map(d -> {
            Product p = productRepository.findById(d.getProductId()).orElse(null);
            String pName = (p != null) ? p.getName() : "N/A";
            String pCode = (p != null) ? p.getCode() : "N/A";

            return new OrderDetailInfo(d.getId().toString(), pCode, pName,
                    d.getQuanity(), d.getPrice(), d.getAmount());
        }).collect(Collectors.toList());
    }


    public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNavigationPage) {
        Pageable pageable = PageRequest.of(page - 1, maxResult, Sort.by("orderDate").descending());
        Page<Order> result = orderRepository.findAll(pageable);

        Page<OrderInfo> infoPage = result.map(o -> new OrderInfo(
                o.getId().toString(),
                o.getOrderDate(),
                o.getOrderNum(),
                o.getAmount(),
                o.getCustomerName(),
                o.getCustomerAddress(),
                o.getCustomerEmail(),
                o.getCustomerPhone(),
                o.getStatus()
        ));

        return new PaginationResult<>(infoPage, maxNavigationPage);
    }


    public void updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}