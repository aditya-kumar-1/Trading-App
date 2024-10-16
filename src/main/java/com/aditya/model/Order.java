package com.aditya.model;

import com.aditya.domain.OrderStatus;
import com.aditya.domain.OrderType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @Column(nullable = false)
    private OrderType orderType;

    @Column(nullable = false)
    private BigDecimal price;


    private LocalDateTime timestamp=LocalDateTime.now();

    @Column(nullable = false)
    private OrderStatus status ;


    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderItem orderItem;
}
