package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.model.dto.PasswordResetRequestDTO;
import org.allisra.ecommerceapp.model.dto.SetNewPasswordDTO;
import org.allisra.ecommerceapp.model.dto.auth.LoginRequest;
import org.allisra.ecommerceapp.model.dto.auth.LoginResponse;
import org.allisra.ecommerceapp.model.dto.user.UserCreateDTO;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.model.entity.VerificationToken;
import org.allisra.ecommerceapp.security.jwt.JwtUtils;
import org.allisra.ecommerceapp.security.userdetails.CustomUserDetails;
import org.allisra.ecommerceapp.service.TokenService;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDTO createDTO) {
        log.debug("Registration request received for email: {}", createDTO.getEmail());
        UserDTO userDTO = userService.createuser(createDTO);

        // Send verification email with UserDTO
        tokenService.createEmailVerificationTokenAndSendEmail(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Registration successful. Please check your email to verify your account.",
                        "userId", userDTO.getId()
                ));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        String email = tokenService.validateEmailVerificationToken(token);
        userService.verifyEmail(email);
        tokenService.deleteToken(token);

        return ResponseEntity.ok("Email verified successfully. You can now login.");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam String email) {
        UserDTO userDTO = userService.getUserByEmail(email);

        if (Boolean.TRUE.equals(userDTO.isEmailVerified())) {
            throw new BadRequestException("Email is already verified");
        }

        tokenService.createEmailVerificationTokenAndSendEmail(userDTO);
        return ResponseEntity.ok("Verification email has been resent");
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequestDTO requestDTO) {
        User user = userService.findUserEntityByEmail(requestDTO.getEmail());
        tokenService.createPasswordResetTokenAndSendEmail(user.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody SetNewPasswordDTO request) {
        String email = tokenService.validatePasswordResetToken(request.getToken());
        userService.resetPassword(email, request.getNewPassword());
        tokenService.deleteToken(request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.debug("Login attempt for email: {}", loginRequest.getEmail());

            // Check if email is verified
            User user = userService.findUserEntityByEmail(loginRequest.getEmail());
            if (!user.isEmailVerified()) {
                throw new BadRequestException("Please verify your email before logging in");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            log.debug("Authentication successful, generating JWT token");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);
            log.debug("JWT token generated successfully");

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            LoginResponse response = new LoginResponse();
            response.setToken(jwt);
            response.setExpiresIn(jwtUtils.getJwtExpirationMs());
            response.setEmail(userDetails.getUsername());
            response.setRoles(roles);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Authentication failed: ", e);
            throw new BadRequestException("Invalid email or password");
        }
    }
}