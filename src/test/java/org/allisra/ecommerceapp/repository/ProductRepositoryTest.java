package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp(){
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(100);
        testProduct.setSku("TEST-SKU-001");
        testProduct.setActive(true);

        // TestEntityManager kullanarak test verisini persist et
        testProduct = testEntityManager.persist(testProduct);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("Should find product by SKU")
    void shouldFindProductBySku(){

        //when
        Optional<Product> found = productRepository.findBySku("TEST-SKU-001");

        //then
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(product -> {
                    assertThat(product.getName()).isEqualTo(testProduct.getName());
                    assertThat(product.getPrice()).isEqualByComparingTo(testProduct.getPrice());
                });

    }

    @Test
    @DisplayName("Should find active products")
    void shouldFindActiveProducts(){
        Product inactiveProduct = new Product();
        inactiveProduct.setName("Inactive Product");
        inactiveProduct.setPrice(new BigDecimal("49.99"));
        inactiveProduct.setStockQuantity(50);
        inactiveProduct.setSku("TEST-SKU-002");
        inactiveProduct.setActive(false);
        testEntityManager.persist(inactiveProduct);
        testEntityManager.flush();

        //when
        List<Product> activeProducts = productRepository.findByActiveTrue();

        //then
        assertThat(activeProducts)
                .hasSize(1)
                .allMatch(Product::isActive)
                .extracting(Product::getName)
                .containsExactly("Test Product");

    }

    @Test
    @DisplayName("Should find products by name containing text")
    void shouldFindProductsByNameContaining() {
        // given
        Product anotherProduct = new Product();
        anotherProduct.setName("Another Test Item");
        anotherProduct.setPrice(new BigDecimal("149.99"));
        anotherProduct.setStockQuantity(75);
        anotherProduct.setSku("TEST-SKU-003");
        anotherProduct.setActive(true);
        testEntityManager.persist(anotherProduct);
        testEntityManager.flush();

        // when
        List<Product> foundProducts = productRepository.findByNameContainingIgnoreCase("test");

        // then
        assertThat(foundProducts)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product", "Another Test Item");
    }

    @Test
    @DisplayName("Should return empty when searching non-existent SKU")
    void shouldReturnEmptyWhenSearchingNonExistentSku() {
        // when
        Optional<Product> notFound = productRepository.findBySku("NON-EXISTENT-SKU");

        // then
        assertThat(notFound).isEmpty();
    }

}
