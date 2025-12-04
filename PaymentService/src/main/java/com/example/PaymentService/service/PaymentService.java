package com.example.PaymentService.service;

import com.example.PaymentService.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> findAllPayments();
    Optional<Payment> findPaymentById(Long id);
    List<Payment> findPaymentsByOrderId(Long orderId);
    Payment updatePayment(Long id, Payment paymentDetails);
    void deletePayment(Long id);
}