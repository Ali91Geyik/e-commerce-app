// src/main/java/org/allisra/ecommerceapp/model/dto/review/ReviewFilterDTO.java

package org.allisra.ecommerceapp.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.allisra.ecommerceapp.model.entity.Review;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFilterDTO {
    private Long productId;
    private Long userId;
    private Integer minRating;
    private Integer maxRating;
    private Review.ReviewStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}