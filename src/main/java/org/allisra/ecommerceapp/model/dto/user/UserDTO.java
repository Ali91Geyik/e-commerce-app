package org.allisra.ecommerceapp.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2-50 characters.")
    @Pattern(regexp = "^[a-zA-ZçğıöşüÇĞİÖŞÜ\\s]+$", message = "First name can only contain letters")
    private String firstName;  // firsName -> firstName düzeltildi

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZçğıöşüÇĞİÖŞÜ\\s]+$", message = "Last name can only contain letters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private Set<String> roleNames;

    private boolean active;
    private boolean emailVerified;

    private java.time.LocalDateTime createdAt;
}