package org.allisra.ecommerceapp.service;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.exception.InvalidTokenException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.exception.TokenExpiredException;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.model.entity.VerificationToken;
import org.allisra.ecommerceapp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${app.token.password-reset.expiration}")
    private int passwordResetExpirationMinutes;

    @Value("${app.token.email-verification.expiration}")
    private int emailVerificationExpirationHours;

    public void createPasswordResetTokenAndSendEmail(User user){
    //Token oluşturma
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setTokenType(VerificationToken.TokenType.PASSWORD_RESET);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(passwordResetExpirationMinutes));

        tokenRepository.save(verificationToken);

        //email gönder
        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("user", user);

        emailService.sendEmail(
                user.getEmail(), "Password Reset Request", "password-reset-email", context
        );

    }

    public void createEmailVerificationTokenAndSendEmail(User user){
        String token= UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setTokenType(VerificationToken.TokenType.EMAIL_VERIFICATION);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(emailVerificationExpirationHours));

        tokenRepository.save(verificationToken);

        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("user", user);

        emailService.sendEmail(user.getEmail(), "Email Verification", "email-verification", context);
    }
    public VerificationToken validateToken(String token, VerificationToken.TokenType tokenType){
    VerificationToken verificationToken = tokenRepository.findByToken(token)
            .orElseThrow(()-> new ResourceNotFoundException("invalid token"));

    if (verificationToken.isExpired()){
        throw new TokenExpiredException("Token has expired");
    }
    if (verificationToken.getTokenType()!= tokenType){
        throw new InvalidTokenException("Token type is invalid");
    }
    return verificationToken;
    }

}
