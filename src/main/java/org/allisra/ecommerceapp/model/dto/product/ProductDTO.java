package org.allisra.ecommerceapp.model.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;

    @NotBlank(message = "product name is required")
    @Size(max = 100, min = 3, message = "product must be between 3-100 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "invalid price format")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity can not be negative")
    private Integer stockQuantity;

    private String sku;
    private String imageUrl;
    private boolean active;

    // Category ilişkisi için
    private Long categoryId; // Kategori ID'sini ekledik
    private String categoryName; // İsteğe bağlı olarak kategori adını da ekleyebiliriz

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}