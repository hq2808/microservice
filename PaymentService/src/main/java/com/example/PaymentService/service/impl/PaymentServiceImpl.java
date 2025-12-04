package com.example.PaymentService.service.impl;

import com.example.PaymentService.model.Payment;
import com.example.PaymentService.repositories.PaymentRepository;
import com.example.PaymentService.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        if (payment.getStatus() == null || payment.getStatus().isEmpty()) {
            payment.setStatus("PENDING");
        }
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> findPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public Payment updatePayment(Long id, Payment paymentDetails) {
        return paymentRepository.findById(id)
                .map(existingPayment -> {
                    existingPayment.setStatus(paymentDetails.getStatus());
                    if (paymentDetails.getTransactionId() != null) {
                        existingPayment.setTransactionId(paymentDetails.getTransactionId());
                    }
                    return paymentRepository.save(existingPayment);
                })
                .orElseThrow(() -> new RuntimeException("Payment not found with id " + id));
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found with id " + id);
        }
        paymentRepository.deleteById(id);
    }
}