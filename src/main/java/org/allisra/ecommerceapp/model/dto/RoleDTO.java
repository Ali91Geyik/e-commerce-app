package org.allisra.ecommerceapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {


    private Long id;

    @NotBlank(message = "Role name is required.")
    @Pattern(
            regexp = "^ROLE_[A-Z]+$",
            message = "Role name must start with 'ROLE_' and contain only uppercase letters"
    )
    @Size(min = 5, max = 30, message = "Role name must be between 5-30 characters.")
    private String name;
}
