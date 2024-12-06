package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.Review;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Ürüne göre yorumları getirme
    Page<Review> findByProductAndStatus(Product product, Review.ReviewStatus status, Pageable pageable);

    // Kullanıcıya göre yorumları getirme
    Page<Review> findByUser(User user, Pageable pageable);

    // Ürün yorumlarının duruma göre sayısı
    Long countByProductAndStatus(Product product, Review.ReviewStatus status);

    // Belirli bir kullanıcının belirli bir ürüne yaptığı yorum kontrolü
    boolean existsByUserAndProduct(User user, Product product);

    // Onay bekleyen yorumları getirme
    Page<Review> findByStatus(Review.ReviewStatus status, Pageable pageable);

    // Bir ürünün ortalama puanını hesaplama
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product AND r.status = 'APPROVED'")
    Optional<Double> findAverageRatingByProduct(@Param("product") Product product);

    // Ürünün onaylanmış yorum sayısını getirme
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product AND r.status = 'APPROVED'")
    Long countApprovedReviewsByProduct(@Param("product") Product product);

    // Belirli bir zaman aralığındaki yorumları getirme
    List<Review> findByCreatedAtBetweenAndStatus(
            LocalDateTime start,
            LocalDateTime end,
            Review.ReviewStatus status);

    // En son yorumları getirme
    List<Review> findTop10ByStatusOrderByCreatedAtDesc(Review.ReviewStatus status);

    // Kullanıcının son yorumlarını getirme
    List<Review> findTop5ByUserAndStatusOrderByCreatedAtDesc(User user, Review.ReviewStatus status);
}
