package com.example.OrderService.service;

import com.example.OrderService.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> findAllOrders();
    Optional<Order> findOrderById(Long id);
    Order updateOrder(Long id, Order orderDetails);
    void deleteOrder(Long id);
    Order updateOrderStatus(Long id, String newStatus);
}
