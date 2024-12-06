// src/main/java/org/allisra/ecommerceapp/model/dto/product/ProductFilterDTO.java

package org.allisra.ecommerceapp.model.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String brand;
    private Long categoryId;
    private BigDecimal minRating;
    private Boolean active;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}