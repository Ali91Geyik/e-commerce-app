package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.address.AddressCreateDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressUpdateDTO;
import org.allisra.ecommerceapp.security.userdetails.CustomUserDetails;
import org.allisra.ecommerceapp.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressCreateDTO createDTO) {
        AddressDTO createdAddress = addressService.createAddress(userDetails.getUser().getId(), createDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressUpdateDTO updateDTO) {
        if (!id.equals(updateDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        AddressDTO updatedAddress = addressService.updateAddress(userDetails.getUser().getId(), updateDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AddressDTO> addresses = addressService.getUserAddresses(userDetails.getUser().getId());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        AddressDTO address = addressService.getAddressById(userDetails.getUser().getId(), id);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        addressService.deleteAddress(userDetails.getUser().getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/default-shipping")
    public ResponseEntity<AddressDTO> setDefaultShippingAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        AddressDTO address = addressService.setDefaultShippingAddress(userDetails.getUser().getId(), id);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/{id}/default-billing")
    public ResponseEntity<AddressDTO> setDefaultBillingAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        AddressDTO address = addressService.setDefaultBillingAddress(userDetails.getUser().getId(), id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/default/shipping")
    public ResponseEntity<AddressDTO> getDefaultShippingAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressDTO address = addressService.getDefaultShippingAddress(userDetails.getUser().getId());
        return ResponseEntity.ok(address);
    }

    @GetMapping("/default/billing")
    public ResponseEntity<AddressDTO> getDefaultBillingAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressDTO address = addressService.getDefaultBillingAddress(userDetails.getUser().getId());
        return ResponseEntity.ok(address);
    }
}