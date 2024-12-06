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
    @Size(min = 3, max = 100, message = "Name must be between 3-100 characters")
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand name cannot exceed 50 characters")
    private String brand;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String sku;
    private String imageUrl;
}