package org.allisra.ecommerceapp.service;

import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.AddressMapper;
import org.allisra.ecommerceapp.model.dto.address.AddressCreateDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressUpdateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.entity.Address;
import org.allisra.ecommerceapp.repository.AddressRepository;
import org.allisra.ecommerceapp.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private UserDTO testUserDTO;
    private Address testAddress;
    private AddressDTO testAddressDTO;
    private AddressCreateDTO createDTO;
    private AddressUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .build();

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setTitle("Home");
        testAddress.setFullName("Test User");
        testAddress.setPhoneNumber("1234567890");
        testAddress.setAddressLine("123 Test St");
        testAddress.setCity("Test City");
        testAddress.setState("Test State");
        testAddress.setCountry("Test Country");
        testAddress.setPostalCode("12345");
        testAddress.setCreatedAt(LocalDateTime.now());

        testAddressDTO = AddressDTO.builder()
                .id(1L)
                .title("Home")
                .fullName("Test User")
                .phoneNumber("1234567890")
                .addressLine("123 Test St")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .postalCode("12345")
                .build();

        createDTO = AddressCreateDTO.builder()
                .title("Home")
                .fullName("Test User")
                .phoneNumber("1234567890")
                .addressLine("123 Test St")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .postalCode("12345")
                .build();

        updateDTO = AddressUpdateDTO.builder()
                .id(1L)
                .title("Updated Home")
                .fullName("Test User")
                .phoneNumber("1234567890")
                .addressLine("123 Test St")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .postalCode("12345")
                .build();
    }

    @Test
    @DisplayName("Should create address successfully")
    void shouldCreateAddress() {
        // given
        given(userService.getUserById(1L)).willReturn(testUserDTO);
        given(addressRepository.countByUser_Id(1L)).willReturn(0L);
        given(addressMapper.createDtoToEntity(createDTO)).willReturn(testAddress);
        given(addressRepository.save(any(Address.class))).willReturn(testAddress);
        given(addressMapper.entityToDto(testAddress)).willReturn(testAddressDTO);

        // when
        AddressDTO result = addressService.createAddress(1L, createDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(testAddressDTO.getTitle());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("Should update address successfully")
    void shouldUpdateAddress() {
        // given
        given(userService.getUserById(1L)).willReturn(testUserDTO);
        given(addressRepository.findByIdAndUser_Id(1L, 1L)).willReturn(Optional.of(testAddress));
        given(addressRepository.save(any(Address.class))).willReturn(testAddress);
        given(addressMapper.entityToDto(testAddress)).willReturn(testAddressDTO);

        // when
        AddressDTO result = addressService.updateAddress(1L, updateDTO);

        // then
        assertThat(result).isNotNull();
        verify(addressMapper).updateEntityFromDto(updateDTO, testAddress);
        verify(addressRepository).save(testAddress);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent address")
    void shouldThrowExceptionWhenUpdatingNonExistentAddress() {
        // given
        given(userService.getUserById(1L)).willReturn(testUserDTO);
        given(addressRepository.findByIdAndUser_Id(1L, 1L)).willReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> addressService.updateAddress(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Address not found");
    }

    @Test
    @DisplayName("Should get user addresses")
    void shouldGetUserAddresses() {
        // given
        given(userService.getUserById(1L)).willReturn(testUserDTO);
        given(addressRepository.findByUser_IdOrderByCreatedAtDesc(1L))
                .willReturn(List.of(testAddress));
        given(addressMapper.entitiesToDtos(List.of(testAddress)))
                .willReturn(List.of(testAddressDTO));

        // when
        List<AddressDTO> result = addressService.getUserAddresses(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testAddressDTO.getTitle());
    }

    @Test
    @DisplayName("Should throw exception when deleting default address")
    void shouldThrowExceptionWhenDeletingDefaultAddress() {
        // given
        testAddress.setDefaultShipping(true);
        given(userService.getUserById(1L)).willReturn(testUserDTO);
        given(addressRepository.findByIdAndUser_Id(1L, 1L)).willReturn(Optional.of(testAddress));
        given(addressRepository.countByUser_Id(1L)).willReturn(1L);

        // when/then
        assertThatThrownBy(() -> addressService.deleteAddress(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot delete the only default address");
    }
}