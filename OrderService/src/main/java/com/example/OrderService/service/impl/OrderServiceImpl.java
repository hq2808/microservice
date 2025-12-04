package com.example.OrderService.service.impl;

import com.example.OrderService.model.Order;
import com.example.OrderService.model.OrderItem;
import com.example.OrderService.repositories.OrderRepository;
import com.example.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
            }
        }

        BigDecimal totalAmount = calculateTotalAmount(order.getItems());
        order.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));

        // [BUSINESS LOGIC]: Gọi InventoryService.reserveStock() ở đây

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order updateOrder(Long id, Order orderDetails) {
        // [BUSINESS LOGIC]: Tại đây, bạn cần gọi InventoryService để:
        // a) Giải phóng (unreserve) số lượng hàng cũ (nếu số lượng giảm).
        // b) Đặt trước (reserve) số lượng hàng mới (nếu số lượng tăng/item mới).
        // Logic này thường được xử lý phức tạp hơn, có thể dùng Saga Pattern.

        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setCustomerId(orderDetails.getCustomerId());
                    existingOrder.setStatus(orderDetails.getStatus());
                    existingOrder.getItems().clear();
                    if (orderDetails.getItems() != null) {
                        existingOrder.getItems().addAll(orderDetails.getItems());
                    }
                    BigDecimal newTotalAmount = calculateTotalAmount(existingOrder.getItems());
                    existingOrder.setTotalAmount(newTotalAmount.setScale(2, RoundingMode.HALF_UP));
                    return orderRepository.save(existingOrder);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void deleteOrder(Long id) {

    }

    @Override
    public Order updateOrderStatus(Long id, String newStatus) {
        return orderRepository.findById(id)
                .map(order -> {
                    // [BUSINESS LOGIC]: Logic chuyển trạng thái (ví dụ: không được chuyển từ DELIVERED sang PENDING)
                    if (order.getStatus().equals("DELIVERED") && newStatus.equals("PENDING")) {
                        throw new IllegalArgumentException("Cannot revert delivered order status.");
                    }
                    order.setStatus(newStatus);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }
}
