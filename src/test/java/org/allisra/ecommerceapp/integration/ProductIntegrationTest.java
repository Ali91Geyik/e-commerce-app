package org.allisra.ecommerceapp.integration;

import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Role;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.repository.ProductRepository;
import org.allisra.ecommerceapp.repository.RoleRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
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
class ProductIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    private String adminToken;
    private ProductCreateDTO createDTO;
    private HttpHeaders headers;

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "Admin123!@#";

    @BeforeEach
    void setUp() {
        // Cleanup
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

        // Prepare test data
        createDTO = new ProductCreateDTO();
        createDTO.setName("Test Product");
        createDTO.setDescription("Test Description");
        createDTO.setPrice(new BigDecimal("99.99"));
        createDTO.setStockQuantity(100);
        createDTO.setSku("TEST-SKU-001");
    }

    @Test
    @DisplayName("Should create and retrieve product")
    void shouldCreateAndRetrieveProduct() {
        // Create product
        HttpEntity<ProductCreateDTO> createRequest = new HttpEntity<>(createDTO, headers);
        ResponseEntity<ProductDTO> createResponse = restTemplate.exchange(
                "/api/v1/products",
                HttpMethod.POST,
                createRequest,
                ProductDTO.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getName()).isEqualTo("Test Product");

        // Retrieve product
        Long productId = createResponse.getBody().getId();
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<ProductDTO> getResponse = restTemplate.exchange(
                "/api/v1/products/" + productId,
                HttpMethod.GET,
                getRequest,
                ProductDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(productId);
        assertThat(getResponse.getBody().getName()).isEqualTo("Test Product");
        assertThat(getResponse.getBody().getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(getResponse.getBody().getStockQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should update product")
    void shouldUpdateProduct() {
        // First create a product
        HttpEntity<ProductCreateDTO> createRequest = new HttpEntity<>(createDTO, headers);
        ResponseEntity<ProductDTO> createResponse = restTemplate.exchange(
                "/api/v1/products",
                HttpMethod.POST,
                createRequest,
                ProductDTO.class
        );

        assertThat(createResponse.getBody()).isNotNull();
        Long productId = createResponse.getBody().getId();

        // Update product
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setId(productId);
        updateDTO.setName("Updated Product");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPrice(new BigDecimal("149.99"));
        updateDTO.setStockQuantity(150);
        updateDTO.setSku("TEST-SKU-001");
        updateDTO.setActive(true);

        HttpEntity<ProductUpdateDTO> updateRequest = new HttpEntity<>(updateDTO, headers);
        ResponseEntity<ProductDTO> updateResponse = restTemplate.exchange(
                "/api/v1/products/" + productId,
                HttpMethod.PUT,
                updateRequest,
                ProductDTO.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getName()).isEqualTo("Updated Product");
        assertThat(updateResponse.getBody().getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(updateResponse.getBody().getStockQuantity()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        // First create a product
        HttpEntity<ProductCreateDTO> createRequest = new HttpEntity<>(createDTO, headers);
        ResponseEntity<ProductDTO> createResponse = restTemplate.exchange(
                "/api/v1/products",
                HttpMethod.POST,
                createRequest,
                ProductDTO.class
        );

        assertThat(createResponse.getBody()).isNotNull();
        Long productId = createResponse.getBody().getId();

        // Delete product
        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/products/" + productId,
                HttpMethod.DELETE,
                deleteRequest,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify product is deleted
        ResponseEntity<ProductDTO> getResponse = restTemplate.exchange(
                "/api/v1/products/" + productId,
                HttpMethod.GET,
                deleteRequest,
                ProductDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should update product stock")
    void shouldUpdateProductStock() {
        // First create a product
        HttpEntity<ProductCreateDTO> createRequest = new HttpEntity<>(createDTO, headers);
        ResponseEntity<ProductDTO> createResponse = restTemplate.exchange(
                "/api/v1/products",
                HttpMethod.POST,
                createRequest,
                ProductDTO.class
        );

        assertThat(createResponse.getBody()).isNotNull();
        Long productId = createResponse.getBody().getId();

        // Update stock
        HttpEntity<Void> updateStockRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> updateStockResponse = restTemplate.exchange(
                String.format("/api/v1/products/%d/stock?quantity=50", productId),
                HttpMethod.PUT,
                updateStockRequest,
                Void.class
        );

        assertThat(updateStockResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify stock update
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<ProductDTO> getResponse = restTemplate.exchange(
                "/api/v1/products/" + productId,
                HttpMethod.GET,
                getRequest,
                ProductDTO.class
        );

        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getStockQuantity()).isEqualTo(150); // Initial 100 + 50
    }
}