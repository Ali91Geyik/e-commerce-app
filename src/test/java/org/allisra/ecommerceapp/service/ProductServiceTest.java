package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.ProductMapper;
import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.repository.ProductRepository;
import org.allisra.ecommerceapp.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductDTO testProductDTO;
    private ProductCreateDTO createDTO;
    private ProductUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(100);
        testProduct.setSku("TEST-SKU-001");

        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setName("Test Product");
        testProductDTO.setPrice(new BigDecimal("99.99"));
        testProductDTO.setStockQuantity(100);
        testProductDTO.setSku("TEST-SKU-001");

        createDTO = new ProductCreateDTO();
        createDTO.setName("New Product");
        createDTO.setPrice(new BigDecimal("149.99"));
        createDTO.setStockQuantity(50);
        createDTO.setSku("NEW-SKU-001");

        updateDTO = new ProductUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(new BigDecimal("199.99"));
        updateDTO.setStockQuantity(75);
        updateDTO.setSku("TEST-SKU-001");
    }

    @Test
    @DisplayName("Should Create Product succesfully")
    void shouldCreateProduct() {

        //given
        given(productRepository.existsBySku(createDTO.getSku())).willReturn(false);
        given(productMapper.createDtoToEntity(createDTO)).willReturn(testProduct);
        given(productRepository.save(any(Product.class))).willReturn(testProduct);
        given(productMapper.entityToDto(testProduct)).willReturn(testProductDTO);

        //when
        ProductDTO result = productService.createProduct(createDTO);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testProductDTO.getName());
        verify(productRepository).save(any(Product.class));

    }

    @Test
    @DisplayName("Should throw exception when creating product with existing SKU")
    void shouldThrowExceptionWhenCreatingProductWithExistingSku() {
        // given
        given(productRepository.existsBySku(createDTO.getSku())).willReturn(true);

        // when/then
        assertThatThrownBy(() -> productService.createProduct(createDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should get product by ID")
    void shouldGetProductById() {
        // given
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productMapper.entityToDto(testProduct)).willReturn(testProductDTO);

        // when
        ProductDTO result = productService.getProductById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);


    }

    @Test
    @DisplayName("Should throw exception when getting non-existent product")
    void shouldThrowExceptionWhenGettingNonExistentProduct() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update stock quantity")
    void shouldUpdateStockQuantity() {
        // given
        int quantityToAdd = 50;
        testProduct.setStockQuantity(100);
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productRepository.save(any(Product.class))).willReturn(testProduct);

        // when
        productService.updateStock(1L, quantityToAdd);

        // then
        verify(productRepository).save(any(Product.class));
        assertThat(testProduct.getStockQuantity()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should throw exception when updating stock below zero")
    void shouldThrowExceptionWhenUpdatingStockBelowZero() {
        // given
        testProduct.setStockQuantity(100);
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

        // when/then
        assertThatThrownBy(() -> productService.updateStock(1L, -150))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock");
    }

}
