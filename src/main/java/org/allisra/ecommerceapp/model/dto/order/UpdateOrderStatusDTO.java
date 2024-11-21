package org.allisra.ecommerceapp.model.dto.order;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusDTO {
    @NotNull(message = "Order ID is required")
    private Long id;

    @NotBlank(message = "Order status is required")
    private String status;

    private String trackingNumber;
}
