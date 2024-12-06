package org.allisra.ecommerceapp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3-100 characters")
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand name cannot exceed 50 characters")
    @Column(nullable = false)
    private String brand = "Default Brand";

    @DecimalMin(value = "0.0", message = "Average rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Average rating cannot exceed 5.0")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Min(value = 0, message = "Review count cannot be negative")
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Min(value = 0, message = "View count cannot be negative")
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Size(max = 2000, message = "Description must be under 2000 characters")
    @Column(length = 2000)
    private String description;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity can't negative")
    @Column(nullable = false)
    private Integer stockQuantity;

    @Size(max = 50, message = "SKU can not exceed 50 characters")
    @Column(unique = true)
    private String sku; //Stock keeping unit

    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt= LocalDateTime.now();
        updatedAt= LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

}
