package com.aditya.service;
import com.aditya.domain.OrderType;
import com.aditya.model.Coin;
import com.aditya.model.Order;
import com.aditya.model.OrderItem;
import com.aditya.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId);

    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

    void cancelOrder(Long orderId);
    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
