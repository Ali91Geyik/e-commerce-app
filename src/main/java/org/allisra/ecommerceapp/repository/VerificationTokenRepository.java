package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {


    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserIdAndTokenType(Long userId, VerificationToken.TokenType tokenType);

    List<VerificationToken> findAllByExpiryDateBefore(LocalDateTime dateTime);
    void deleteByToken(String token);

}
