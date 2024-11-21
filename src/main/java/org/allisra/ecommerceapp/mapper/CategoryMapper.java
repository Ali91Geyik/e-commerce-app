package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.category.CategoryCreateDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryTreeDTO;
import org.allisra.ecommerceapp.model.dto.category.CategoryUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "subCategories", ignore = true)
    CategoryDTO entityToDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Category createDtoToEntity(CategoryCreateDTO createDTO);

    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(CategoryUpdateDTO updateDTO, @MappingTarget Category category);

    List<CategoryDTO> entitiesToDtos(List<Category> categories);

    @Mapping(target = "children", source = "subCategories")
    CategoryTreeDTO entityToTreeDto(Category category);

    List<CategoryTreeDTO> entitiesToTreeDtos(List<Category> categories);

    @AfterMapping
    default void mapChildren(@MappingTarget CategoryTreeDTO target, Category source) {
        if (source.getSubCategories() != null && !source.getSubCategories().isEmpty()) {
            target.setChildren(entitiesToTreeDtos(source.getSubCategories()));
        }
    }
}