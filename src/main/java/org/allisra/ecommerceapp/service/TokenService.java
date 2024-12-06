package org.allisra.ecommerceapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.exception.InvalidTokenException;
import org.allisra.ecommerceapp.exception.TokenExpiredException;
import org.allisra.ecommerceapp.model.dto.user.UserDTO;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.model.entity.VerificationToken;
import org.allisra.ecommerceapp.repository.UserRepository;
import org.allisra.ecommerceapp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Value("${app.token.email-verification.expiration:24}")
    private int emailVerificationExpirationHours;

    @Value("${app.token.password-reset.expiration:1}")
    private int passwordResetExpirationHours;

    @Transactional
    public void createEmailVerificationTokenAndSendEmail(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Varolan token'ı kontrol et ve gerekirse sil
        tokenRepository.findByUserIdAndTokenType(user.getId(), VerificationToken.TokenType.EMAIL_VERIFICATION)
                .ifPresent(tokenRepository::delete);

        // Yeni token oluştur
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(generateToken());
        token.setTokenType(VerificationToken.TokenType.EMAIL_VERIFICATION);
        token.setExpiryDate(LocalDateTime.now().plusHours(emailVerificationExpirationHours));
        token.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token);
        log.info("Email verification token created for user: {}", user.getEmail());

        // Email gönder
        emailService.sendVerificationEmail(user, token.getToken());
    }

    @Transactional
    public void createPasswordResetTokenAndSendEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Varolan token'ı kontrol et ve gerekirse sil
        tokenRepository.findByUserIdAndTokenType(user.getId(), VerificationToken.TokenType.PASSWORD_RESET)
                .ifPresent(tokenRepository::delete);

        // Yeni token oluştur
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(generateToken());
        token.setTokenType(VerificationToken.TokenType.PASSWORD_RESET);
        token.setExpiryDate(LocalDateTime.now().plusHours(passwordResetExpirationHours));
        token.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token);
        log.info("Password reset token created for user: {}", user.getEmail());

        // Email gönder
        emailService.sendPasswordResetEmail(user, token.getToken());
    }

    @Transactional(readOnly = true)
    public String validateEmailVerificationToken(String token) {
        return validateToken(token, VerificationToken.TokenType.EMAIL_VERIFICATION);
    }

    @Transactional(readOnly = true)
    public String validatePasswordResetToken(String token) {
        return validateToken(token, VerificationToken.TokenType.PASSWORD_RESET);
    }

    private String validateToken(String token, VerificationToken.TokenType tokenType) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Token not found: {}", token);
                    return new InvalidTokenException("Invalid or expired token");
                });

        if (verificationToken.getTokenType() != tokenType) {
            log.error("Invalid token type. Expected: {}, Found: {}", tokenType, verificationToken.getTokenType());
            throw new InvalidTokenException("Invalid token type");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.error("Token expired: {}", token);
            throw new TokenExpiredException("Token has expired");
        }

        return verificationToken.getUser().getEmail();
    }

    @Transactional
    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
        log.info("Token deleted: {}", token);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}