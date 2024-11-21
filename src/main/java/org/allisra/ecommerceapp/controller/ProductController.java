package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id){
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku){
        ProductDTO productDTO = productService.getProductBySku(sku);
        return ResponseEntity.ok(productDTO);
    }
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String search){
        List<ProductDTO> productDTOS;

        if (search!=null && !search.trim().isEmpty()){
            productDTOS = productService.searchProducts(search);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            productDTOS = productService.getActiveProducts();
        } else {
            productDTOS = productService.getAllProducts();
        }
        return ResponseEntity.ok(productDTOS);
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
