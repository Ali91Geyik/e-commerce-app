package org.allisra.ecommerceapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.AddressMapper;
import org.allisra.ecommerceapp.model.dto.address.AddressCreateDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressUpdateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.entity.Address;
import org.allisra.ecommerceapp.repository.AddressRepository;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.service.AddressService;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public AddressDTO createAddress(Long userId, AddressCreateDTO createDTO) {
        // Kullanıcının varlığını kontrol et
        UserDTO userDTO = userService.getUserById(userId);
        if (userDTO == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Address address = addressMapper.createDtoToEntity(createDTO);
        address.setUser(userRepository.getReferenceById(userId)); // Sadece ID referansı al

        // İlk adres kontrolü
        if (addressRepository.countByUser_Id(userId) == 0) {
            address.setDefaultShipping(true);
            address.setDefaultBilling(true);
        } else {
            // Varsayılan gönderim adresi kontrolü
            if (createDTO.isDefaultShipping()) {
                addressRepository.findByUser_IdAndDefaultShippingTrue(userId)
                        .ifPresent(defaultAddress -> {
                            defaultAddress.setDefaultShipping(false);
                            addressRepository.save(defaultAddress);
                        });
            }
            // Varsayılan fatura adresi kontrolü
            if (createDTO.isDefaultBilling()) {
                addressRepository.findByUser_IdAndDefaultBillingTrue(userId)
                        .ifPresent(defaultAddress -> {
                            defaultAddress.setDefaultBilling(false);
                            addressRepository.save(defaultAddress);
                        });
            }
        }

        Address savedAddress = addressRepository.save(address);
        return addressMapper.entityToDto(savedAddress);
    }

    @Override
    public AddressDTO updateAddress(Long userId, AddressUpdateDTO updateDTO) {
        // Kullanıcının varlığını kontrol et
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address address = addressRepository.findByIdAndUser_Id(updateDTO.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (updateDTO.isDefaultShipping() && !address.isDefaultShipping()) {
            addressRepository.findByUser_IdAndDefaultShippingTrue(userId)
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setDefaultShipping(false);
                        addressRepository.save(defaultAddress);
                    });
        }
        if (updateDTO.isDefaultBilling() && !address.isDefaultBilling()) {
            addressRepository.findByUser_IdAndDefaultBillingTrue(userId)
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setDefaultBilling(false);
                        addressRepository.save(defaultAddress);
                    });
        }

        addressMapper.updateEntityFromDto(updateDTO, address);
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.entityToDto(updatedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Long userId, Long addressId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address address = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        return addressMapper.entityToDto(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses(Long userId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        List<Address> addresses = addressRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return addressMapper.entitiesToDtos(addresses);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address address = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        long addressCount = addressRepository.countByUser_Id(userId);
        if (addressCount == 1 && (address.isDefaultShipping() || address.isDefaultBilling())) {
            throw new BadRequestException("Cannot delete the only default address");
        }

        addressRepository.delete(address);
    }

    @Override
    public AddressDTO setDefaultShippingAddress(Long userId, Long addressId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address newDefault = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.findByUser_IdAndDefaultShippingTrue(userId)
                .ifPresent(currentDefault -> {
                    currentDefault.setDefaultShipping(false);
                    addressRepository.save(currentDefault);
                });

        newDefault.setDefaultShipping(true);
        return addressMapper.entityToDto(addressRepository.save(newDefault));
    }

    @Override
    public AddressDTO setDefaultBillingAddress(Long userId, Long addressId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address newDefault = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.findByUser_IdAndDefaultBillingTrue(userId)
                .ifPresent(currentDefault -> {
                    currentDefault.setDefaultBilling(false);
                    addressRepository.save(currentDefault);
                });

        newDefault.setDefaultBilling(true);
        return addressMapper.entityToDto(addressRepository.save(newDefault));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getDefaultShippingAddress(Long userId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address address = addressRepository.findByUser_IdAndDefaultShippingTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No default shipping address found"));
        return addressMapper.entityToDto(address);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getDefaultBillingAddress(Long userId) {
        if (!userService.getUserById(userId).getId().equals(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Address address = addressRepository.findByUser_IdAndDefaultBillingTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No default billing address found"));
        return addressMapper.entityToDto(address);
    }
}