package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByPaymentId(Long paymentId);

    List<PaymentTransaction> findByPaymentIdAndStatus(Long paymentId, PaymentTransaction.TransactionStatus status);

    List<PaymentTransaction> findByTransactionReference(String transactionReference);
}