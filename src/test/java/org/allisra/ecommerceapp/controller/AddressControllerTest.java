package org.allisra.ecommerceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.allisra.ecommerceapp.model.dto.address.AddressCreateDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressDTO;
import org.allisra.ecommerceapp.model.dto.address.AddressUpdateDTO;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.security.userdetails.CustomUserDetails;
import org.allisra.ecommerceapp.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    private AddressDTO testAddressDTO;
    private AddressCreateDTO createDTO;
    private AddressUpdateDTO updateDTO;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        userDetails = new CustomUserDetails(testUser);

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
    @DisplayName("Should create address")
    @WithMockUser
    void shouldCreateAddress() throws Exception {
        given(addressService.createAddress(eq(1L), any(AddressCreateDTO.class)))
                .willReturn(testAddressDTO);

        mockMvc.perform(post("/api/v1/addresses")
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testAddressDTO.getId()))
                .andExpect(jsonPath("$.title").value(testAddressDTO.getTitle()));
    }

    @Test
    @DisplayName("Should get user addresses")
    @WithMockUser
    void shouldGetUserAddresses() throws Exception {
        List<AddressDTO> addresses = Arrays.asList(testAddressDTO);
        given(addressService.getUserAddresses(1L)).willReturn(addresses);

        mockMvc.perform(get("/api/v1/addresses")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testAddressDTO.getId()))
                .andExpect(jsonPath("$[0].title").value(testAddressDTO.getTitle()));
    }

    @Test
    @DisplayName("Should update address")
    @WithMockUser
    void shouldUpdateAddress() throws Exception {
        given(addressService.updateAddress(eq(1L), any(AddressUpdateDTO.class)))
                .willReturn(testAddressDTO);

        mockMvc.perform(put("/api/v1/addresses/{id}", 1L)
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddressDTO.getId()))
                .andExpect(jsonPath("$.title").value(testAddressDTO.getTitle()));
    }

    @Test
    @DisplayName("Should delete address")
    @WithMockUser
    void shouldDeleteAddress() throws Exception {
        mockMvc.perform(delete("/api/v1/addresses/{id}", 1L)
                        .with(csrf())
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress(1L, 1L);
    }

    @Test
    @DisplayName("Should set default shipping address")
    @WithMockUser
    void shouldSetDefaultShippingAddress() throws Exception {
        given(addressService.setDefaultShippingAddress(1L, 1L))
                .willReturn(testAddressDTO);

        mockMvc.perform(put("/api/v1/addresses/{id}/default-shipping", 1L)
                        .with(csrf())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddressDTO.getId()));
    }

    @Test
    @DisplayName("Should get default shipping address")
    @WithMockUser
    void shouldGetDefaultShippingAddress() throws Exception {
        given(addressService.getDefaultShippingAddress(1L))
                .willReturn(testAddressDTO);

        mockMvc.perform(get("/api/v1/addresses/default/shipping")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddressDTO.getId()))
                .andExpect(jsonPath("$.title").value(testAddressDTO.getTitle()));
    }

    @Test
    @DisplayName("Should get default billing address")
    @WithMockUser
    void shouldGetDefaultBillingAddress() throws Exception {
        given(addressService.getDefaultBillingAddress(1L))
                .willReturn(testAddressDTO);

        mockMvc.perform(get("/api/v1/addresses/default/billing")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddressDTO.getId()))
                .andExpect(jsonPath("$.title").value(testAddressDTO.getTitle()));
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/addresses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 when creating address with invalid data")
    @WithMockUser
    void shouldReturn400WhenCreatingAddressWithInvalidData() throws Exception {
        AddressCreateDTO invalidDTO = AddressCreateDTO.builder().build();

        mockMvc.perform(post("/api/v1/addresses")
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}