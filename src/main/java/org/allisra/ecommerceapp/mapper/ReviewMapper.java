// src/main/java/org/allisra/ecommerceapp/mapper/ReviewMapper.java

package org.allisra.ecommerceapp.mapper;

import org.allisra.ecommerceapp.model.dto.review.ReviewCreateDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    ReviewDTO entityToDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review createDtoToEntity(ReviewCreateDTO createDTO);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ReviewUpdateDTO updateDTO, @MappingTarget Review review);

    List<ReviewDTO> entitiesToDtos(List<Review> reviews);
}