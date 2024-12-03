package org.allisra.ecommerceapp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADDRESSES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Title is required")
    @Size(max = 50)
    private String title;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank(message = "Address line is required")
    @Size(max = 255)
    private String addressLine;

    @NotBlank(message = "City is required")
    @Size(max = 50)
    private String city;

    @NotBlank(message = "State/Province is required")
    @Size(max = 50)
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 50)
    private String country;

    @NotBlank(message = "Postal code is required")
    @Size(max = 10)
    private String postalCode;

    @Column(name = "is_default_shipping")
    private boolean defaultShipping = false;

    @Column(name = "is_default_billing")
    private boolean defaultBilling = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}