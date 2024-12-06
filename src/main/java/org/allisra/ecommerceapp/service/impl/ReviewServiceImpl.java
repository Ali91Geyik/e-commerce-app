// src/main/java/org/allisra/ecommerceapp/service/impl/ReviewServiceImpl.java

package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.ReviewMapper;
import org.allisra.ecommerceapp.model.dto.review.ReviewCreateDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewResponseDTO;
import org.allisra.ecommerceapp.model.dto.review.ReviewUpdateDTO;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.model.entity.Review;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.repository.ProductRepository;
import org.allisra.ecommerceapp.repository.ReviewRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDTO createReview(Long userId, ReviewCreateDTO createDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(createDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("User has already reviewed this product");
        }

        Review review = reviewMapper.createDtoToEntity(createDTO);
        review.setUser(user);
        review.setProduct(product);
        review.setStatus(Review.ReviewStatus.PENDING);

        Review savedReview = reviewRepository.save(review);
        updateProductRatingAndReviewCount(product);

        return reviewMapper.entityToDto(savedReview);
    }

    @Override
    public ReviewDTO updateReview(Long userId, ReviewUpdateDTO updateDTO) {
        Review review = reviewRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("User can only update their own reviews");
        }

        reviewMapper.updateEntityFromDto(updateDTO, review);
        review.setStatus(Review.ReviewStatus.PENDING); // Updated reviews need re-approval
        Review updatedReview = reviewRepository.save(review);
        updateProductRatingAndReviewCount(review.getProduct());

        return reviewMapper.entityToDto(updatedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return reviewMapper.entityToDto(review);
    }

    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("User can only delete their own reviews");
        }

        reviewRepository.delete(review);
        updateProductRatingAndReviewCount(review.getProduct());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return reviewRepository.findByProductAndStatus(product, Review.ReviewStatus.APPROVED, pageable)
                .map(reviewMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return reviewRepository.findByUser(user, pageable)
                .map(reviewMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByStatus(Review.ReviewStatus.PENDING, pageable)
                .map(reviewMapper::entityToDto);
    }

    @Override
    public ReviewDTO approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setStatus(Review.ReviewStatus.APPROVED);
        Review approvedReview = reviewRepository.save(review);
        updateProductRatingAndReviewCount(review.getProduct());

        return reviewMapper.entityToDto(approvedReview);
    }

    @Override
    public ReviewDTO rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setStatus(Review.ReviewStatus.REJECTED);
        Review rejectedReview = reviewRepository.save(review);
        updateProductRatingAndReviewCount(review.getProduct());

        return reviewMapper.entityToDto(rejectedReview);
    }

    @Override
    public List<ReviewDTO> getRecentReviews(int limit) {
        return null;
    }

    @Override
    public Double getProductAverageRating(Long productId) {
        return null;
    }

    @Override
    public Long getProductReviewCount(Long productId) {
        return null;
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return false;
    }

    @Override
    public List<ReviewDTO> getReviewsByDateRange(LocalDateTime start, LocalDateTime end, Review.ReviewStatus status) {
        return null;
    }

    private void updateProductRatingAndReviewCount(Product product) {
        Double averageRating = reviewRepository.findAverageRatingByProduct(product)
                .orElse(0.0);
        Long reviewCount = reviewRepository.countApprovedReviewsByProduct(product);

        product.setAverageRating(BigDecimal.valueOf(averageRating));
        product.setReviewCount(reviewCount.intValue());
        productRepository.save(product);
    }
    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO.ProductReviewStats getProductReviewStats(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<Review> approvedReviews = reviewRepository.findByProductAndStatus(
                product,
                Review.ReviewStatus.APPROVED,
                Pageable.unpaged()
        ).getContent();

        int totalReviews = approvedReviews.size();
        Map<Integer, Long> ratingCounts = approvedReviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        return ReviewResponseDTO.ProductReviewStats.builder()
                .averageRating(product.getAverageRating().doubleValue())
                .totalReviews(totalReviews)
                .fiveStarCount(ratingCounts.getOrDefault(5, 0L).intValue())
                .fourStarCount(ratingCounts.getOrDefault(4, 0L).intValue())
                .threeStarCount(ratingCounts.getOrDefault(3, 0L).intValue())
                .twoStarCount(ratingCounts.getOrDefault(2, 0L).intValue())
                .oneStarCount(ratingCounts.getOrDefault(1, 0L).intValue())
                .build();
    }

}