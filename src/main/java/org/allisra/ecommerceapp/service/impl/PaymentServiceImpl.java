package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.PaymentMapper;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.CreatePaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Order;
import org.allisra.ecommerceapp.model.entity.Payment;
import org.allisra.ecommerceapp.model.entity.Payment.PaymentStatus;
import org.allisra.ecommerceapp.model.entity.PaymentTransaction;
import org.allisra.ecommerceapp.repository.OrderRepository;
import org.allisra.ecommerceapp.repository.PaymentRepository;
import org.allisra.ecommerceapp.repository.PaymentTransactionRepository;
import org.allisra.ecommerceapp.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDTO createPayment(CreatePaymentDTO createDTO) {
        log.info("Creating payment for order ID: {}", createDTO.getOrderId());

        // Sipariş kontrolü
        Order order = orderRepository.findById(createDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Ödeme metodu kontrolü
        if (!validatePaymentMethod(createDTO.getPaymentMethod())) {
            throw new BadRequestException("Invalid payment method");
        }

        // Ödeme oluşturma
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(createDTO.getPaymentMethod()));
        payment.setTransactionId(generateTransactionId());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", savedPayment.getId());

        return paymentMapper.entityToDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdWithTransactions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return paymentMapper.entityToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order"));
        return paymentMapper.entityToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return paymentMapper.entitiesToDtos(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return paymentMapper.entitiesToDtos(payments);
    }

    @Override
    @Transactional
    public PaymentDTO updatePaymentStatus(PaymentUpdateDTO updateDTO) {
        Payment payment = paymentRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.valueOf(updateDTO.getStatus()));
        if (updateDTO.getTransactionId() != null) {
            payment.setTransactionId(updateDTO.getTransactionId());
        }

        return paymentMapper.entityToDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Burada gerçek ödeme işlemi simüle ediliyor
        try {
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);

            // İşlem kaydı oluştur
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setPayment(payment);
            transaction.setStatus(PaymentTransaction.TransactionStatus.SUCCESS);
            transaction.setType(PaymentTransaction.TransactionType.PAYMENT);
            transaction.setTransactionReference(generateTransactionId());
            transactionRepository.save(transaction);

            // Ödeme başarılı
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BadRequestException("Payment processing failed");
        }
    }

    @Override
    @Transactional
    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BadRequestException("Only completed payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        // Refund işlem kaydı
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setStatus(PaymentTransaction.TransactionStatus.SUCCESS);
        transaction.setType(PaymentTransaction.TransactionType.REFUND);
        transaction.setTransactionReference(generateTransactionId());
        transactionRepository.save(transaction);

        return paymentMapper.entityToDto(savedPayment);
    }

    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Only pending payments can be cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        // İptal işlem kaydı
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setStatus(PaymentTransaction.TransactionStatus.SUCCESS);
        transaction.setType(PaymentTransaction.TransactionType.CHARGEBACK);
        transaction.setTransactionReference(generateTransactionId());
        transactionRepository.save(transaction);
    }

    @Override
    public boolean validatePaymentMethod(String paymentMethod) {
        return Arrays.stream(Payment.PaymentMethod.values())
                .anyMatch(method -> method.name().equals(paymentMethod));
    }

    @Override
    public boolean isPaymentCompleted(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .orElse(false);
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}