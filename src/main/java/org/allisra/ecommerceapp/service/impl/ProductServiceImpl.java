package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.ProductMapper;
import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.repository.ProductRepository;
import org.allisra.ecommerceapp.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public ProductDTO createProduct(ProductCreateDTO createDTO) {
        if (createDTO.getSku()!=null && productRepository.existsBySku(createDTO.getSku())){
            throw new BadRequestException("Product with SKU: "+ createDTO.getSku()+" already exists");
        }
        Product product = productMapper.createDtoToEntity(createDTO);
        Product savedProduct= productRepository.save(product);
        return productMapper.entityToDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Product not found with id: "+id ));
        return productMapper.entityToDto(product);
    }

    @Override
    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku).orElseThrow(
                ()-> new ResourceNotFoundException("Product not found with SKU: "+ sku)
        );
        return productMapper.entityToDto(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO> getAllProducts() {
        return productMapper.entitiesToDtos(productRepository.findAll());
    }

    @Override
    public List<ProductDTO> getActiveProducts() {
        return productMapper.entitiesToDtos(productRepository.findByActiveTrue());
    }

    @Override
    public List<ProductDTO> searchProducts(String name) {
        return productMapper.entitiesToDtos(productRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public ProductDTO updateProduct(ProductUpdateDTO updateDTO) {
        Product existingProduct = productRepository.findById(updateDTO.getId())
                .orElseThrow(()->new ResourceNotFoundException("Product not found with this id: "+updateDTO.getId()));
        if (updateDTO.getSku()!=null && updateDTO.getSku().equals(existingProduct.getSku()) &&
                productRepository.existsBySku(updateDTO.getSku())){
            throw new BadRequestException("Product with that SKU: " +updateDTO.getSku()+" already exists ");
        }
        productMapper.updateEntityFromDto(updateDTO,existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.entityToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)){
        throw new ResourceNotFoundException("Product not found with id: " + id);
    }
    productRepository.deleteById(id);
    }

    @Override
    public void updateStock(Long id, int quantity) {
    Product product = productRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Product not found with id: "+id));
    if (product.getStockQuantity()+quantity<0){
        throw  new BadRequestException("Insufficient stock for product: "+ product.getName());
    }
    product.setStockQuantity(product.getStockQuantity()+quantity);
    productRepository.save(product);
    }
}
