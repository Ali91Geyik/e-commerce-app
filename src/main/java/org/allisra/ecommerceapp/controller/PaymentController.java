package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.CreatePaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Payment.PaymentStatus;
import org.allisra.ecommerceapp.security.userdetails.CustomUserDetails;
import org.allisra.ecommerceapp.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(
            @Valid @RequestBody CreatePaymentDTO createDTO) {
        PaymentDTO payment = paymentService.createPayment(createDTO);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<PaymentDTO>> getUserPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userDetails.getUser().getId()));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @Valid @RequestBody PaymentUpdateDTO updateDTO) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(updateDTO));
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<Void> processPayment(@PathVariable Long id) {
        paymentService.processPayment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Boolean> isPaymentCompleted(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.isPaymentCompleted(orderId));
    }
}