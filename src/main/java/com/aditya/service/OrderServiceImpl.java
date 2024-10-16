package com.aditya.service;

import com.aditya.domain.OrderStatus;
import com.aditya.domain.OrderType;
import com.aditya.model.*;
import com.aditya.repository.OrderItemRepository;
import com.aditya.repository.OrderRepository;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WalletService walletService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private AssetService assetService;
    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price=orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();
        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);


        return orderRepository.save(order);
    }


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
       return orderRepository.findByOrderId(userId);


    }

    @Override
    public void cancelOrder(Long orderId) {

    }


    public OrderItem createOrderItem(Coin coin,double quantity,
                                     double buyPrice, double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }
    @Transactional
    public Order buyAssests(Coin coin,double quantity, OrderType orderType, User user) throws Exception {
        if(quantity<=0)
        {
            throw new Exception("Quantity should be > 0");
        }
        double buyPrice= coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin,quantity,buyPrice,0);
        Order order = createOrder(user,orderItem,orderType.BUY);
        orderItem.setOrder(order);
        walletService.payOrderPayment(order,user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderType(orderType.BUY);
        Order savedOrder = orderRepository.save(order);

        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(
                order.getUser().getId(),
                order.getOrderItem().getCoin().getId()
        );
        if(oldAsset==null)
        {
            assetService.createAsset(user,orderItem.getCoin(),orderItem.getQuantity());
        }
        else
        {
            assetService.updateAsset(oldAsset.getId(), quantity);
        }


        return savedOrder;

    }
    @Transactional
    public Order sellAssests(Coin coin,double quantity, OrderType orderType, User user) throws Exception {
        if(quantity<=0)
        {
            throw new Exception("Quantity should be > 0");
        }
        double sellPrice= coin.getCurrentPrice();
        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());
        double buyPrice = assetToSell.getBuyPrice();
        if(assetToSell!=null) {
            OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);



            Order order = createOrder(user, orderItem, orderType.SELL);
            orderItem.setOrder(order);
            if (assetToSell.getQuantity() >= quantity) {
                order.setStatus(OrderStatus.SUCCESS);
                order.setOrderType(orderType.SELL);
                Order savedOrder = orderRepository.save(order);
                walletService.payOrderPayment(order, user);
                Asset updatedAsset=assetService.updateAsset(assetToSell.getId(), -quantity);
                if (updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1) {
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            }


            throw new Exception("Insufficient quantity to send");
        }
        throw new Exception("Asset Not Found");
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception {

        if(orderType.equals(OrderType.BUY))
        {
            return buyAssests(coin,quantity,orderType,user);
        } else if (orderType.equals(OrderType.SELL)) {
            return sellAssests(coin,quantity,orderType,user);
        }
        throw new Exception("Invalid order type");
    }
}
