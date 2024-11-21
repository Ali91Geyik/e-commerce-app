package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;

import java.util.List;

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



}
