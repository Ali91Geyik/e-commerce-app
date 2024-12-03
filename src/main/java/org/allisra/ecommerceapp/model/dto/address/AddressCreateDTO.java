package org.allisra.ecommerceapp.model.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateDTO {
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

    private boolean defaultShipping;
    private boolean defaultBilling;
}