package org.allisra.ecommerceapp.model.dto.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;
    private String title;
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private boolean defaultShipping;
    private boolean defaultBilling;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}