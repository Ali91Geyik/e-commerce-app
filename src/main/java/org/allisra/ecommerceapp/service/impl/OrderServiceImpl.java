package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.OrderMapper;
import org.allisra.ecommerceapp.model.dto.order.CreateOrderDTO;
import org.allisra.ecommerceapp.model.dto.order.OrderDTO;
import org.allisra.ecommerceapp.model.dto.order.UpdateOrderStatusDTO;
import org.allisra.ecommerceapp.model.entity.*;
import org.allisra.ecommerceapp.repository.CartRepository;
import org.allisra.ecommerceapp.repository.OrderRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDTO createOrder(CreateOrderDTO createDTO) {
        // Sepeti bul ve kontrol et
        Cart cart = cartRepository.findByIdWithItems(createDTO.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + createDTO.getCartId()));

        if (cart.getStatus() != Cart.CartStatus.ACTIVE) {
            throw new BadRequestException("Cart is not active");
        }

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Yeni sipariş oluştur
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(createDTO.getShippingAddress());
        order.setBillingAddress(createDTO.getBillingAddress());
        order.setPaymentMethod(createDTO.getPaymentMethod());

        // Sepet öğelerini sipariş öğelerine dönüştür
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // Toplam tutarı hesapla
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        // Siparişi kaydet
        Order savedOrder = orderRepository.save(order);

        // Sepeti checked-out durumuna güncelle
        cart.setStatus(Cart.CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return orderMapper.entityToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.entityToDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(orderMapper::entityToDto);
    }

    @Override
    public OrderDTO updateOrderStatus(UpdateOrderStatusDTO updateDTO) {
        Order order = orderRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + updateDTO.getId()));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(updateDTO.getStatus().toUpperCase());
            order.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + updateDTO.getStatus());
        }

        if (updateDTO.getTrackingNumber() != null) {
            order.setTrackingNumber(updateDTO.getTrackingNumber());
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.entityToDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Can only delete orders in PENDING status");
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrdersByStatus(Long userId, String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByUserIdAndStatus(userId, orderStatus)
                    .stream()
                    .map(orderMapper::entityToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }
    }
}