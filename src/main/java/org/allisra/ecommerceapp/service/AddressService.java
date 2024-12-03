package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.model.dto.address.AddressCreateDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressUpdateDTO;
import java.util.List;

public interface AddressService {
    AddressDTO createAddress(Long userId, AddressCreateDTO createDTO);

    AddressDTO updateAddress(Long userId, AddressUpdateDTO updateDTO);

    AddressDTO getAddressById(Long userId, Long addressId);

    List<AddressDTO> getUserAddresses(Long userId);

    void deleteAddress(Long userId, Long addressId);

    AddressDTO setDefaultShippingAddress(Long userId, Long addressId);

    AddressDTO setDefaultBillingAddress(Long userId, Long addressId);

    AddressDTO getDefaultShippingAddress(Long userId);

    AddressDTO getDefaultBillingAddress(Long userId);
}