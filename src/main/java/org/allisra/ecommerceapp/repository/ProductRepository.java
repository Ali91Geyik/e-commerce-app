package org.allisra.ecommerceapp.repository;


import org.allisra.ecommerceapp.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findByActiveTrue();
    List<Product> findByNameContainingIgnoreCase(String name);

    // Yeni sort ve filter metodları
    @Query("SELECT p FROM Product p WHERE " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:brand IS NULL OR p.brand = :brand) AND " +
            "(:minRating IS NULL OR p.averageRating >= :minRating) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:active IS NULL OR p.active = :active)")
    Page<Product> findWithFilters(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("brand") String brand,
            @Param("minRating") BigDecimal minRating,
            @Param("categoryId") Long categoryId,
            @Param("active") Boolean active,
            Pageable pageable);

    // Marka listesini getiren metod
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true ORDER BY p.brand")
    List<String> findAllActiveBrands();

    // Ortalama puan ve yorum sayısı istatistikleri
    @Query("SELECT p.brand, AVG(p.averageRating), COUNT(p) " +
            "FROM Product p WHERE p.active = true GROUP BY p.brand")
    List<Object[]> findBrandStatistics();

    // En çok görüntülenen ürünler
    List<Product> findTop10ByActiveOrderByViewCountDesc(boolean active);

    // En çok yorum alan ürünler
    List<Product> findTop10ByActiveOrderByReviewCountDesc(boolean active);

    // En yüksek puanlı ürünler
    List<Product> findTop10ByActiveOrderByAverageRatingDesc(boolean active);
}



