package org.allisra.ecommerceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;
import org.allisra.ecommerceapp.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDTO productDTO;
    private ProductCreateDTO createDTO;
    private ProductUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(new BigDecimal("99.99"));
        productDTO.setStockQuantity(100);
        productDTO.setSku("TEST-SKU-001");
        productDTO.setActive(true);

        createDTO = new ProductCreateDTO();
        createDTO.setName("New Product");
        createDTO.setDescription("New Description");
        createDTO.setPrice(new BigDecimal("149.99"));
        createDTO.setStockQuantity(50);
        createDTO.setSku("NEW-SKU-001");

        updateDTO = new ProductUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setName("Updated Product");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPrice(new BigDecimal("199.99"));
        updateDTO.setStockQuantity(75);
        updateDTO.setSku("TEST-SKU-001");
    }

    @Test
    @DisplayName("Should get product by id")
    void shouldGetProductById() throws Exception {
        // given
        given(productService.getProductById(1L)).willReturn(productDTO);

        // when & then
        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    @DisplayName("Should get all products")
    void shouldGetAllProducts() throws Exception {
        // given
        List<ProductDTO> products = Arrays.asList(productDTO);
        given(productService.getAllProducts()).willReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create product when admin")
    void shouldCreateProductWhenAdmin() throws Exception {
        // given
        given(productService.createProduct(any(ProductCreateDTO.class))).willReturn(productDTO);

        // when & then
        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should forbid product creation when not admin")
    void shouldForbidProductCreationWhenNotAdmin() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update product when admin")
    void shouldUpdateProductWhenAdmin() throws Exception {
        // given
        given(productService.updateProduct(any(ProductUpdateDTO.class))).willReturn(productDTO);

        // when & then
        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete product when admin")
    void shouldDeleteProductWhenAdmin() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/products/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should search products by name")
    void shouldSearchProductsByName() throws Exception {
        // given
        List<ProductDTO> searchResults = Arrays.asList(productDTO);
        given(productService.searchProducts("Test")).willReturn(searchResults);

        // when & then
        mockMvc.perform(get("/api/v1/products")
                        .param("search", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

}
