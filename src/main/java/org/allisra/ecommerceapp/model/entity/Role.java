package org.allisra.ecommerceapp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Role name is required")
    @Pattern(
            regexp = "^ROLE_[A-Z]+$",
            message = "Role name must start with 'ROLE_' and contain only uppercase letters"
    )
    @Size(min =5, max = 30, message = "Role must be between 5 and 30 characters")
    @Column(nullable = false, unique = true)
    private String name;  //Örn: ROLE_USER, ROLE ADMIN


}
