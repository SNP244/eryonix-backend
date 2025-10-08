package com.eryonix.repository;  

import com.eryonix.model.PasswordResetToken;  
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Find by token string
    Optional<PasswordResetToken> findByToken(String token);

    // Clean up expired or used tokens (optional)
    long deleteByUsedIsTrueOrExpiryTimeBefore(LocalDateTime now);
}
