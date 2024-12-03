package org.allisra.ecommerceapp.model.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDTO {
        private Long id;
        private Long orderId;
        private BigDecimal amount;
        private String status;
        private String paymentMethod;
        private String transactionId;
        private List<PaymentTransactionDTO> transactions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentTransactionDTO {
        private Long id;
        private Long paymentId;
        private String status;
        private String type;
        private String transactionReference;
        private String errorCode;
        private String errorMessage;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentDTO {
        @NotNull(message = "Order ID is required")
        private Long orderId;

        @NotNull(message = "Payment method is required")
        private String paymentMethod;

        private CreditCardDTO creditCard;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditCardDTO {
        @NotNull(message = "Card number is required")
        private String cardNumber;

        @NotNull(message = "Expiry month is required")
        private String expiryMonth;

        @NotNull(message = "Expiry year is required")
        private String expiryYear;

        @NotNull(message = "CVV is required")
        private String cvv;

        @NotNull(message = "Card holder name is required")
        private String cardHolderName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentUpdateDTO {
        @NotNull(message = "Payment ID is required")
        private Long id;

        @NotNull(message = "Status is required")
        private String status;

        private String transactionId;
    }
}