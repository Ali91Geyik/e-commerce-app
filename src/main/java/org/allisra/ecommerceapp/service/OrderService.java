package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.order.CreateOrderDTO;
import org.allisra.ecommerceapp.model.dto.order.OrderDTO;
import org.allisra.ecommerceapp.model.dto.order.UpdateOrderStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(CreateOrderDTO createDTO);
    OrderDTO getOrderById(Long id);
    Page<OrderDTO> getUserOrders(Long userId, Pageable pageable);
    OrderDTO updateOrderStatus(UpdateOrderStatusDTO updateDTO);
    void deleteOrder(Long id);
    List<OrderDTO> getUserOrdersByStatus(Long userId, String status);
}