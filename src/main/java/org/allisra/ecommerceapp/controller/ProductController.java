package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.product.*;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductCreateDTO createDTO){
        ProductDTO createdProduct= productService.createProduct(createDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        List<ProductDTO> products = activeOnly != null && activeOnly
                ? productService.getActiveProducts()
                : productService.getAllProducts();

        int totalItems = products.size();
        int totalPages = (int) Math.ceil(totalItems / (double) size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<ProductDTO> pageContent = products.subList(fromIndex, toIndex);

        Map<String, Object> response = Map.of(
                "items", pageContent,
                "currentPage", page,
                "totalItems", totalItems,
                "totalPages", totalPages
        );

        return ResponseEntity.ok(response);
    }
    // Yeni endpoint'ler
    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDTO>> getProductsWithFilters(
            @ModelAttribute ProductFilterDTO filterDTO) {
        return ResponseEntity.ok(productService.getProductsWithFilters(filterDTO));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        return ResponseEntity.ok(productService.getAllBrands());
    }

    @GetMapping("/brands/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, ProductStatsDTO>> getBrandStatistics() {
        return ResponseEntity.ok(productService.getBrandStatistics());
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<ProductDTO>> getMostViewedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getMostViewedProducts(limit));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductDTO>> getTopRatedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getTopRatedProducts(limit));
    }

    @GetMapping("/most-reviewed")
    public ResponseEntity<List<ProductDTO>> getMostReviewedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getMostReviewedProducts(limit));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<List<ProductDTO>> getRelatedProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(productService.getRelatedProducts(id, limit));
    }

    @GetMapping("/category/{categoryId}/price-range")
    public ResponseEntity<Map<String, Double>> getPriceRangeByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getPriceRangeByCategory(categoryId));
    }

    // Admin-specific endpoints
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        ProductUpdateDTO updateDTO = ProductUpdateDTO.builder()
                .id(id)
                .active(active)
                .build();
        return ResponseEntity.ok(productService.updateProduct(updateDTO));
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboardStats() {
        Map<String, Object> stats = Map.of(
                "brands", productService.getAllBrands(),
                "brandStats", productService.getBrandStatistics(),
                "topRated", productService.getTopRatedProducts(5),
                "mostViewed", productService.getMostViewedProducts(5),
                "mostReviewed", productService.getMostReviewedProducts(5)
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<Page<ProductDTO>> advancedSearch(
            @ModelAttribute ProductFilterDTO filterDTO) {
        return ResponseEntity.ok(productService.getProductsWithFilters(filterDTO));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        if(!id.equals(productUpdateDTO.getId())){
            return ResponseEntity.badRequest().build();
        }
        ProductDTO updatedProduct = productService.updateProduct(productUpdateDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam int quantity
    ){
    productService.updateStock(id, quantity);
    return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
    }

}
