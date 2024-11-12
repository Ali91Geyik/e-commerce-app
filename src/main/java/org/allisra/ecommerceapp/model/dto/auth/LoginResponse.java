package org.allisra.ecommerceapp.model.dto.auth;

import lombok.Data;

import java.util.Set;

@Data
public class LoginResponse{
    private String token;
    private String type="Bearer";
    private Long expiresIn;
    private String email;
    private Set<String> roles;

}
