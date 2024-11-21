package org.allisra.ecommerceapp.model.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @NotNull(message = "User ID is required for update")
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZçğıöşüÇĞİÖŞÜ\\s]+$", message = "First name can only contain letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZçğıöşüÇĞİÖŞÜ\\s]+$", message = "Last name can only contain letters")
    private String lastName;

    @NotEmpty(message = "At least one role must be specified")
    private Set<String> roleNames;

    private boolean active;

}
