// src/main/java/org/allisra/ecommerceapp/controller/ReviewController.java

package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.review.*;
import org.allisra.ecommerceapp.security.userdetails.CustomUserDetails;
import org.allisra.ecommerceapp.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewCreateDTO createDTO) {
        ReviewDTO review = reviewService.createReview(userDetails.getUser().getId(), createDTO);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getProductReviews(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReviewDTO>> getUserReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getUserReviews(userDetails.getUser().getId(), pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateDTO updateDTO) {
        if (!id.equals(updateDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reviewService.updateReview(userDetails.getUser().getId(), updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        reviewService.deleteReview(userDetails.getUser().getId(), id);
        return ResponseEntity.noContent().build();
    }

    // Admin endpoints
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReviewDTO>> getPendingReviews(Pageable pageable) {
        return ResponseEntity.ok(reviewService.getPendingReviews(pageable));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> rejectReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.rejectReview(id));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDTO>> getRecentReviews(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reviewService.getRecentReviews(limit));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ReviewResponseDTO.ProductReviewStats> getProductReviewStats(
            @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviewStats(productId));
    }
}