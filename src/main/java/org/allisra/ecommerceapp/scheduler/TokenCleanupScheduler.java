package org.allisra.ecommerceapp.scheduler;

import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.entity.VerificationToken;
import org.allisra.ecommerceapp.repository.VerificationTokenRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final VerificationTokenRepository tokenRepository;
    public void cleanupExpiredTokens(){
        LocalDateTime now = LocalDateTime.now();
        List<VerificationToken> expiredTokens= tokenRepository.findAllByExpiryDateBefore(now);
        tokenRepository.deleteAll(expiredTokens);
    }

}
