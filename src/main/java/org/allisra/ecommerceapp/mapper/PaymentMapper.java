package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentDTO;
import org.allisra.ecommerceapp.model.dto.payment.PaymentDTOs.PaymentTransactionDTO;
import org.allisra.ecommerceapp.model.entity.Payment;
import org.allisra.ecommerceapp.model.entity.PaymentTransaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "transactions", source = "transactions")
    PaymentDTO entityToDto(Payment payment);

    @Mapping(target = "paymentId", source = "payment.id")
    PaymentTransactionDTO transactionEntityToDto(PaymentTransaction transaction);

    List<PaymentDTO> entitiesToDtos(List<Payment> payments);

    List<PaymentTransactionDTO> transactionEntitiesToDtos(List<PaymentTransaction> transactions);

    @AfterMapping
    default void addTransactions(@MappingTarget PaymentDTO dto, Payment entity) {
        if (entity.getTransactions() != null && !entity.getTransactions().isEmpty()) {
            dto.setTransactions(transactionEntitiesToDtos(entity.getTransactions()));
        }
    }
}