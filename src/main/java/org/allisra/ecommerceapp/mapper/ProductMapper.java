package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.product.ProductCreateDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDTO entityToDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product createDtoToEntity(ProductCreateDTO createDTO);

    // CartServiceImpl için gerekli olan DTO -> Entity dönüşümü
    Product dtoToEntity(ProductDTO productDTO);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ProductUpdateDTO updateDTO, @MappingTarget Product product);

    List<ProductDTO> entitiesToDtos(List<Product> products);

    @AfterMapping
    default void afterDtoToEntity(@MappingTarget Product product) {
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
    }
}