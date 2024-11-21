package org.allisra.ecommerceapp.service;
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
import org.allisra.ecommerceapp.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Cart testCart;
    private Order testOrder;
    private OrderDTO testOrderDTO;
    private CreateOrderDTO createOrderDTO;
    private UpdateOrderStatusDTO updateOrderStatusDTO;
    private Product testProduct;
    private CartItem testCartItem;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // Test User
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        // Test Product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        // Test Cart Item
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);
        testCartItem.setPrice(new BigDecimal("99.99"));

        // Test Cart
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setStatus(Cart.CartStatus.ACTIVE);
        testCart.setCartItems(List.of(testCartItem));

        // Test Order Item
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(new BigDecimal("99.99"));

        // Test Order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setOrderItems(List.of(testOrderItem));
        testOrder.setTotalAmount(new BigDecimal("199.98"));
        testOrder.setShippingAddress("Test Shipping Address");
        testOrder.setBillingAddress("Test Billing Address");
        testOrder.setPaymentMethod("Credit Card");

        // Test Order DTO
        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setUserId(1L);
        testOrderDTO.setStatus("PENDING");
        testOrderDTO.setTotalAmount(new BigDecimal("199.98"));

        // Create Order DTO
        createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCartId(1L);
        createOrderDTO.setShippingAddress("Test Shipping Address");
        createOrderDTO.setBillingAddress("Test Billing Address");
        createOrderDTO.setPaymentMethod("Credit Card");

        // Update Order Status DTO
        updateOrderStatusDTO = new UpdateOrderStatusDTO();
        updateOrderStatusDTO.setId(1L);
        updateOrderStatusDTO.setStatus("CONFIRMED");
        updateOrderStatusDTO.setTrackingNumber("TRACK123");
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrder() {
        // given
        given(cartRepository.findByIdWithItems(createOrderDTO.getCartId())).willReturn(Optional.of(testCart));
        given(orderRepository.save(any(Order.class))).willReturn(testOrder);
        given(orderMapper.entityToDto(testOrder)).willReturn(testOrderDTO);

        // when
        OrderDTO result = orderService.createOrder(createOrderDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testOrderDTO.getId());
        assertThat(result.getTotalAmount()).isEqualByComparingTo(testOrderDTO.getTotalAmount());
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when creating order with non-existent cart")
    void shouldThrowExceptionWhenCreatingOrderWithNonExistentCart() {
        // given
        given(cartRepository.findByIdWithItems(createOrderDTO.getCartId())).willReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> orderService.createOrder(createOrderDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cart not found");
    }

    @Test
    @DisplayName("Should get order by ID")
    void shouldGetOrderById() {
        // given
        given(orderRepository.findByIdWithItems(1L)).willReturn(Optional.of(testOrder));
        given(orderMapper.entityToDto(testOrder)).willReturn(testOrderDTO);

        // when
        OrderDTO result = orderService.getOrderById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testOrderDTO.getId());
        verify(orderRepository).findByIdWithItems(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent order")
    void shouldThrowExceptionWhenGettingNonExistentOrder() {
        // given
        given(orderRepository.findByIdWithItems(999L)).willReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    @DisplayName("Should get user orders")
    void shouldGetUserOrders() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(orderRepository.findByUserOrderByCreatedAtDesc(testUser, pageable)).willReturn(orderPage);
        given(orderMapper.entityToDto(testOrder)).willReturn(testOrderDTO);

        // when
        Page<OrderDTO> result = orderService.getUserOrders(1L, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(testOrderDTO.getId());
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        // given
        given(orderRepository.findById(updateOrderStatusDTO.getId())).willReturn(Optional.of(testOrder));
        given(orderRepository.save(any(Order.class))).willReturn(testOrder);
        given(orderMapper.entityToDto(testOrder)).willReturn(testOrderDTO);

        // when
        OrderDTO result = orderService.updateOrderStatus(updateOrderStatusDTO);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent order status")
    void shouldThrowExceptionWhenUpdatingNonExistentOrderStatus() {
        // given
        given(orderRepository.findById(updateOrderStatusDTO.getId())).willReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> orderService.updateOrderStatus(updateOrderStatusDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    @DisplayName("Should throw exception when updating order with invalid status")
    void shouldThrowExceptionWhenUpdatingOrderWithInvalidStatus() {
        // given
        updateOrderStatusDTO.setStatus("INVALID_STATUS");
        given(orderRepository.findById(updateOrderStatusDTO.getId())).willReturn(Optional.of(testOrder));

        // when/then
        assertThatThrownBy(() -> orderService.updateOrderStatus(updateOrderStatusDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid order status");
    }

    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrder() {
        // given
        given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));

        // when
        orderService.deleteOrder(1L);

        // then
        verify(orderRepository).delete(testOrder);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-PENDING order")
    void shouldThrowExceptionWhenDeletingNonPendingOrder() {
        // given
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));

        // when/then
        assertThatThrownBy(() -> orderService.deleteOrder(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Can only delete orders in PENDING status");
    }

    @Test
    @DisplayName("Should get user orders by status")
    void shouldGetUserOrdersByStatus() {
        // given
        List<Order> orders = List.of(testOrder);
        given(orderRepository.findByUserIdAndStatus(1L, Order.OrderStatus.PENDING)).willReturn(orders);
        given(orderMapper.entityToDto(testOrder)).willReturn(testOrderDTO);

        // when
        List<OrderDTO> result = orderService.getUserOrdersByStatus(1L, "PENDING");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testOrderDTO.getId());
    }

    @Test
    @DisplayName("Should throw exception when getting orders with invalid status")
    void shouldThrowExceptionWhenGettingOrdersWithInvalidStatus() {
        // when/then
        assertThatThrownBy(() -> orderService.getUserOrdersByStatus(1L, "INVALID_STATUS"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid order status");
    }
}