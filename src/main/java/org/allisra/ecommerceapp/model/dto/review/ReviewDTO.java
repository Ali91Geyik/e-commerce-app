// src/main/java/org/allisra/ecommerceapp/model/dto/review/ReviewDTO.java

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
public class ReviewDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private String userFullName; // Kullanıcı adı-soyadı
    private Integer rating;
    private String comment;
    private Review.ReviewStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}