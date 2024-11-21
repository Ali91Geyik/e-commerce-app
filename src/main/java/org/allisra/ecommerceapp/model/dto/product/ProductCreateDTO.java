package org.allisra.ecommerceapp.model.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3-100 characters.")
    private String name;

    @Size(max = 2000, message = "description can not exceed 2000 characters.")
    private String description;

    @NotNull(message = "price value is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "price can not be negative")
    @Digits(integer = 10, fraction = 2, message = "invalid price format")
    private BigDecimal price;

    @NotNull(message = "Stock Quantity number is required")
    @Min(value = 0, message = "Stock quantity can not be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID is required")
    private Long categoryId;  // Kategori ID'sini ekledik

    private String sku;
    private String imageUrl;
}