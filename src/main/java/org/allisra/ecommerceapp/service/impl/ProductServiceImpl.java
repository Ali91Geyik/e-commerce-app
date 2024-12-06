package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.ProductMapper;
import org.allisra.ecommerceapp.model.dto.product.*;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.repository.CategoryRepository;
import org.allisra.ecommerceapp.repository.ProductRepository;
import org.allisra.ecommerceapp.repository.ReviewRepository;
import org.allisra.ecommerceapp.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final ProductMapper productMapper;


    @Override
    public ProductDTO createProduct(ProductCreateDTO createDTO) {
        if (createDTO.getSku()!=null && productRepository.existsBySku(createDTO.getSku())){
            throw new BadRequestException("Product with SKU: "+ createDTO.getSku()+" already exists");
        }
        // Eğer marka belirtilmemişse varsayılan marka ata
        if (createDTO.getBrand() == null || createDTO.getBrand().trim().isEmpty()) {
            createDTO.setBrand("Default Brand");
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
    }@Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsWithFilters(ProductFilterDTO filterDTO) {
        Pageable pageable = createPageable(filterDTO);

        Page<Product> products = productRepository.findWithFilters(
                filterDTO.getMinPrice(),
                filterDTO.getMaxPrice(),
                filterDTO.getBrand(),
                filterDTO.getMinRating(),
                filterDTO.getCategoryId(),
                filterDTO.getActive(),
                pageable
        );

        return products.map(productMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productRepository.findAllActiveBrands();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, ProductStatsDTO> getBrandStatistics() {
        List<Object[]> stats = productRepository.findBrandStatistics();
        Map<String, ProductStatsDTO> result = new HashMap<>();

        for (Object[] stat : stats) {
            String brand = (String) stat[0];
            Double avgRating = (Double) stat[1];
            Long count = (Long) stat[2];

            result.put(brand, ProductStatsDTO.builder()
                    .brand(brand)
                    .averageRating(BigDecimal.valueOf(avgRating))
                    .productCount(count)
                    .build());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getMostViewedProducts(int limit) {
        return productRepository.findTop10ByActiveOrderByViewCountDesc(true)
                .stream()
                .limit(limit)
                .map(productMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getTopRatedProducts(int limit) {
        return null;
    }

    @Override
    public List<ProductDTO> getMostReviewedProducts(int limit) {
        return null;
    }

    @Override
    public void incrementViewCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Map<String, Object> details = new HashMap<>();
        details.put("product", productMapper.entityToDto(product));
        details.put("averageRating", product.getAverageRating());
        details.put("reviewCount", product.getReviewCount());
        details.put("category", product.getCategory().getName());

        return details;
    }

    @Override
    public List<ProductDTO> getRelatedProducts(Long productId, int limit) {
        return null;
    }

    @Override
    public Map<String, Double> getPriceRangeByCategory(Long categoryId) {
        return null;
    }

    private Pageable createPageable(ProductFilterDTO filterDTO) {
        String sortBy = filterDTO.getSortBy() != null ? filterDTO.getSortBy() : "createdAt";
        Sort.Direction direction = filterDTO.getSortDirection() != null &&
                filterDTO.getSortDirection().equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(
                filterDTO.getPage() != null ? filterDTO.getPage() : 0,
                filterDTO.getSize() != null ? filterDTO.getSize() : 10,
                sort
        );
    }
}
