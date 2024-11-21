package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.cart.CartDTO;
import org.allisra.ecommerceapp.model.dto.cart.CartItemDTO;
import org.allisra.ecommerceapp.model.entity.Cart;
import org.allisra.ecommerceapp.model.entity.CartItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    @Mapping(target = "totalItems", expression = "java(calculateTotalItems(cart))")
    @Mapping(target = "status", source = "status")
    CartDTO entityToDto(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "totalPrice", expression = "java(calculateItemTotalPrice(cartItem))")
    CartItemDTO itemEntityToDto(CartItem cartItem);

    List<CartItemDTO> itemEntitiesToDtos(List<CartItem> cartItems);

    @Named("calculateTotalPrice")
    default BigDecimal calculateTotalPrice(Cart cart) {
        if (cart.getCartItems() == null) return BigDecimal.ZERO;
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("calculateTotalItems")
    default int calculateTotalItems(Cart cart) {
        if (cart.getCartItems() == null) return 0;
        return cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Named("calculateItemTotalPrice")
    default BigDecimal calculateItemTotalPrice(CartItem cartItem) {
        return cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
    }
}