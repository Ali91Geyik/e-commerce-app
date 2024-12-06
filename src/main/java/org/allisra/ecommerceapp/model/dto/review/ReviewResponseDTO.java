// src/main/java/org/allisra/ecommerceapp/model/dto/review/ReviewResponseDTO.java

package org.allisra.ecommerceapp.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.allisra.ecommerceapp.model.entity.Review;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private List<ReviewDTO> reviews;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrevious;
    private ProductReviewStats stats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductReviewStats {
        private Double averageRating;
        private Integer totalReviews;
        private Integer fiveStarCount;
        private Integer fourStarCount;
        private Integer threeStarCount;
        private Integer twoStarCount;
        private Integer oneStarCount;
    }
}