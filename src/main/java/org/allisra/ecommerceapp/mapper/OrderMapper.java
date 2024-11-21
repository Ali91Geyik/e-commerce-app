package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.order.OrderDTO;
import org.allisra.ecommerceapp.model.dto.order.OrderItemDTO;
import org.allisra.ecommerceapp.model.entity.Order;
import org.allisra.ecommerceapp.model.entity.OrderItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "status", source = "status")
    OrderDTO entityToDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "totalPrice", expression = "java(calculateItemTotalPrice(orderItem))")
    OrderItemDTO itemEntityToDto(OrderItem orderItem);

    List<OrderItemDTO> itemEntitiesToDtos(List<OrderItem> orderItems);

    @Named("calculateItemTotalPrice")
    default BigDecimal calculateItemTotalPrice(OrderItem orderItem) {
        return orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
    }
}