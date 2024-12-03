package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.CreatePaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Payment.PaymentStatus;

import java.util.List;

public interface PaymentService {

    PaymentDTO createPayment(CreatePaymentDTO createDTO);

    PaymentDTO getPaymentById(Long id);

    PaymentDTO getPaymentByOrderId(Long orderId);

    List<PaymentDTO> getPaymentsByUserId(Long userId);

    List<PaymentDTO> getPaymentsByStatus(PaymentStatus status);

    PaymentDTO updatePaymentStatus(PaymentUpdateDTO updateDTO);

    void processPayment(Long paymentId);

    PaymentDTO refundPayment(Long paymentId);

    void cancelPayment(Long paymentId);

    boolean validatePaymentMethod(String paymentMethod);

    boolean isPaymentCompleted(Long orderId);
}