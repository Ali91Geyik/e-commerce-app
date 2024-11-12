package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.allisra.ecommerceapp.model.dto.PasswordResetRequestDTO;
import org.allisra.ecommerceapp.model.dto.SetNewPasswordDTO;
import org.allisra.ecommerceapp.model.dto.auth.LoginRequest;
import org.allisra.ecommerceapp.model.dto.auth.LoginResponse;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.security.jwt.JwtUtils;
import org.allisra.ecommerceapp.security.userdetails.CustomUserDetails;
import org.allisra.ecommerceapp.service.TokenService;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequestDTO requestDTO){
        User user = userService.findUserEntityByEmail(requestDTO.getEmail());
        tokenService.createPasswordResetTokenAndSendEmail(user);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody SetNewPasswordDTO request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        //Security context update
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //JWT Token oluşturma
        String jwt = jwtUtils.generateJwtToken(authentication);

        //Kullanıcı Detayı alma
        CustomUserDetails userDetails= (CustomUserDetails) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        //Response Oluşturma
        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setExpiresIn(jwtUtils.getJwtExpirationMs());
        response.setEmail(userDetails.getUsername());
        response.setRoles(roles);

        return ResponseEntity.ok(response);

    }

}
