package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.review.ReviewCreateDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewResponseDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(Long userId, ReviewCreateDTO createDTO);
    ReviewDTO updateReview(Long userId, ReviewUpdateDTO updateDTO);
    ReviewDTO getReviewById(Long reviewId);
    void deleteReview(Long userId, Long reviewId);
    Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable);
    Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable);
    Page<ReviewDTO> getPendingReviews(Pageable pageable);
    ReviewDTO approveReview(Long reviewId);
    ReviewDTO rejectReview(Long reviewId);
    List<ReviewDTO> getRecentReviews(int limit);
    Double getProductAverageRating(Long productId);
    Long getProductReviewCount(Long productId);
    boolean hasUserReviewedProduct(Long userId, Long productId);
    List<ReviewDTO> getReviewsByDateRange(LocalDateTime start, LocalDateTime end, Review.ReviewStatus status);
    ReviewResponseDTO.ProductReviewStats getProductReviewStats(Long productId);
}