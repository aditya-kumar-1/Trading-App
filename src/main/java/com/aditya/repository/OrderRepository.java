package com.aditya.repository;

import com.aditya.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long > {
    List<Order> findByOrderId(Long orderId);
}
