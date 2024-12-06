// src/main/java/org/allisra/ecommerceapp/model/dto/product/ProductStatsDTO.java

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
public class ProductStatsDTO {
    private String brand;
    private BigDecimal averageRating;
    private Long productCount;
    private BigDecimal averagePrice;
    private Long totalReviews;
}