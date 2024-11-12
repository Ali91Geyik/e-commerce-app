package org.allisra.ecommerceapp.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static org.allisra.ecommerceapp.model.entity.VerificationToken.TokenType;

import java.time.LocalDateTime;

@Entity
@Table(name = "VERIFICATION_TOKENS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum TokenType{
        PASSWORD_RESET,
        EMAIL_VERIFICATION
    }

    public boolean isExpired(){
    return LocalDateTime.now().isAfter(this.expiryDate);
    }

    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
    public static VerificationToken createToken(User user, TokenType tokenType, int expirationTime){
        return VerificationToken.builder()
                .user(user)
                .token(java.util.UUID.randomUUID().toString())
                .tokenType(tokenType)
                .expiryDate(LocalDateTime.now().plusMinutes(expirationTime))
                .build();
    }

}
