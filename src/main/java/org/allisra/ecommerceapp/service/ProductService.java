package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.product.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductDTO createProduct(ProductCreateDTO createDTO);
    ProductDTO getProductById(Long id);
    ProductDTO getProductBySku(String sku);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getActiveProducts();
    List<ProductDTO> searchProducts(String name);
    ProductDTO updateProduct(ProductUpdateDTO updateDTO);
    void deleteProduct(Long id);
    void updateStock(Long id, int quantity);

    // Yeni metodlar
    Page<ProductDTO> getProductsWithFilters(ProductFilterDTO filterDTO);
    List<String> getAllBrands();
    Map<String, ProductStatsDTO> getBrandStatistics();
    List<ProductDTO> getMostViewedProducts(int limit);
    List<ProductDTO> getTopRatedProducts(int limit);
    List<ProductDTO> getMostReviewedProducts(int limit);
    void incrementViewCount(Long productId);
    Map<String, Object> getProductDetails(Long productId);
    List<ProductDTO> getRelatedProducts(Long productId, int limit);
    Map<String, Double> getPriceRangeByCategory(Long categoryId);


}
