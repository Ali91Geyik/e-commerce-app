package org.allisra.ecommerceapp.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "e-mail is required")
    @Email(message = "Please provide a valid email adress")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

}
