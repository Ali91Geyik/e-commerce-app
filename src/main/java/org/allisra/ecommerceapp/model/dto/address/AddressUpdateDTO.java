package org.allisra.ecommerceapp.model.dto.address;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AddressUpdateDTO extends AddressCreateDTO {
    @NotNull(message = "Address ID is required")
    private Long id;
}