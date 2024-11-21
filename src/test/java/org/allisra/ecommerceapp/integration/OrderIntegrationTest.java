package org.allisra.ecommerceapp.integration;

import org.allisra.ecommerceapp.model.dto.cart.AddToCartDTO;
import org.allisra.ecommerceapp.model.dto.cart.CartDTO;
import org.allisra.ecommerceapp.model.dto.order.CreateOrderDTO;
import org.allisra.ecommerceapp.model.dto.order.OrderDTO;
import org.allisra.ecommerceapp.model.dto.order.UpdateOrderStatusDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    private String adminToken;
    private HttpHeaders headers;
    private ProductDTO testProduct;
    private CartDTO testCart;

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "Admin123!@#";

    @BeforeEach
    void setUp() {
        // Cleanup
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Create admin role
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    return roleRepository.save(role);
                });

        // Create admin user
        User adminUser = new User();
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRoles(Set.of(adminRole));
        adminUser.setActive(true);
        adminUser.setEmailVerified(true);
        userRepository.save(adminUser);

        // Get authentication token
        adminToken = authenticationHelper.getAuthToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Setup headers with authentication
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        // Create test product
        ProductCreateDTO productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setName("Test Product");
        productCreateDTO.setDescription("Test Description");
        productCreateDTO.setPrice(new BigDecimal("99.99"));
        productCreateDTO.setStockQuantity(100);
        productCreateDTO.setSku("TEST-SKU-001");

        HttpEntity<ProductCreateDTO> productRequest = new HttpEntity<>(productCreateDTO, headers);
        ResponseEntity<ProductDTO> productResponse = restTemplate.exchange(
                "/api/v1/products",
                HttpMethod.POST,
                productRequest,
                ProductDTO.class
        );
        testProduct = productResponse.getBody();

        // Add product to cart
        AddToCartDTO addToCartDTO = new AddToCartDTO();
        addToCartDTO.setProductId(testProduct.getId());
        addToCartDTO.setQuantity(2);

        HttpEntity<AddToCartDTO> cartRequest = new HttpEntity<>(addToCartDTO, headers);
        ResponseEntity<CartDTO> cartResponse = restTemplate.exchange(
                "/api/v1/carts/items",
                HttpMethod.POST,
                cartRequest,
                CartDTO.class
        );
        testCart = cartResponse.getBody();
    }

    @Test
    @DisplayName("Should create order from cart")
    void shouldCreateOrderFromCart() {
        // given
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCartId(testCart.getId());
        createOrderDTO.setShippingAddress("Test Shipping Address");
        createOrderDTO.setBillingAddress("Test Billing Address");
        createOrderDTO.setPaymentMethod("Credit Card");

        HttpEntity<CreateOrderDTO> request = new HttpEntity<>(createOrderDTO, headers);

        // when
        ResponseEntity<OrderDTO> response = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                request,
                OrderDTO.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("PENDING");
        assertThat(response.getBody().getTotalAmount())
                .isEqualByComparingTo(testCart.getTotalPrice());
    }

    @Test
    @DisplayName("Should get order by ID")
    void shouldGetOrderById() {
        // First create an order
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCartId(testCart.getId());
        createOrderDTO.setShippingAddress("Test Shipping Address");
        createOrderDTO.setBillingAddress("Test Billing Address");
        createOrderDTO.setPaymentMethod("Credit Card");

        HttpEntity<CreateOrderDTO> createRequest = new HttpEntity<>(createOrderDTO, headers);
        ResponseEntity<OrderDTO> createResponse = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                createRequest,
                OrderDTO.class
        );

        assertThat(createResponse.getBody()).isNotNull();
        Long orderId = createResponse.getBody().getId();

        // Get order
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<OrderDTO> getResponse = restTemplate.exchange(
                "/api/v1/orders/" + orderId,
                HttpMethod.GET,
                getRequest,
                OrderDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        // First create an order
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCartId(testCart.getId());
        createOrderDTO.setShippingAddress("Test Shipping Address");
        createOrderDTO.setBillingAddress("Test Billing Address");
        createOrderDTO.setPaymentMethod("Credit Card");

        HttpEntity<CreateOrderDTO> createRequest = new HttpEntity<>(createOrderDTO, headers);
        ResponseEntity<OrderDTO> createResponse = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                createRequest,
                OrderDTO.class
        );

        assertThat(createResponse.getBody()).isNotNull();
        Long orderId = createResponse.getBody().getId();

        // Update order status
        UpdateOrderStatusDTO updateStatusDTO = new UpdateOrderStatusDTO();
        updateStatusDTO.setId(orderId);
        updateStatusDTO.setStatus("CONFIRMED");
        updateStatusDTO.setTrackingNumber("TRACK123");

        HttpEntity<UpdateOrderStatusDTO> updateRequest = new HttpEntity<>(updateStatusDTO, headers);
        ResponseEntity<OrderDTO> updateResponse = restTemplate.exchange(
                "/api/v1/orders/status",
                HttpMethod.PUT,
                updateRequest,
                OrderDTO.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("Should get user orders")
    void shouldGetUserOrders() {
        // First create an order
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCartId(testCart.getId());
        createOrderDTO.setShippingAddress("Test Shipping Address");
        createOrderDTO.setBillingAddress("Test Billing Address");
        createOrderDTO.setPaymentMethod("Credit Card");

        HttpEntity<CreateOrderDTO> createRequest = new HttpEntity<>(createOrderDTO, headers);
        restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                createRequest,
                OrderDTO.class
        );

        // Get user orders
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<OrderDTO[]> getResponse = restTemplate.exchange(
                "/api/v1/orders/user/" + userRepository.findByEmail(ADMIN_EMAIL).get().getId(),
                HttpMethod.GET,
                getRequest,
                OrderDTO[].class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody()).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Should delete pending order")
    void shouldDeletePendingOrder() {
        // First create an order
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCartId(testCart.getId());
        createOrderDTO.setShippingAddress("Test Shipping Address");
        createOrderDTO.setBillingAddress("Test Billing Address");
        createOrderDTO.setPaymentMethod("Credit Card");

        HttpEntity<CreateOrderDTO> createRequest = new HttpEntity<>(createOrderDTO, headers);
        ResponseEntity<OrderDTO> createResponse = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                createRequest,
                OrderDTO.class
        );

        assertThat(createResponse.getBody()).isNotNull();
        Long orderId = createResponse.getBody().getId();

        // Delete order
        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/orders/" + orderId,
                HttpMethod.DELETE,
                deleteRequest,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify order is deleted
        ResponseEntity<OrderDTO> getResponse = restTemplate.exchange(
                "/api/v1/orders/" + orderId,
                HttpMethod.GET,
                deleteRequest,
                OrderDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}