package org.allisra.ecommerceapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email adress")
    private String email;


}
