package com.eryonix.service;

import com.eryonix.model.PasswordResetToken;
import com.eryonix.model.User;
import com.eryonix.repository.PasswordResetTokenRepository;
import com.eryonix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.reset.base-url}")
    private String resetBaseUrl;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public String createPasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Always return success to avoid exposing emails
            return null;
        }

        User user = userOpt.get();

        // Generate unique token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        // Build frontend reset link, trim to remove accidental spaces
        String resetLink = resetBaseUrl.trim() + token;

        // Send email
        String subject = "Eryonix — Password Reset";
        String body = "Hi " + (user.getFullname() != null ? user.getFullname() : user.getUsername()) + ",\n\n"
                + "We received a request to reset your password.\n"
                + "Click the link below to set a new password (valid for 30 minutes):\n\n"
                + resetLink + "\n\n"
                + "If you didn't request this, you can safely ignore this email.\n\n"
                + "— Eryonix Team";

        emailService.sendPlainText(user.getEmail(), subject, body);

        return token;
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token.trim());
        if (tokenOpt.isEmpty()) throw new RuntimeException("Invalid reset token");

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired");
        if (resetToken.isUsed())
            throw new RuntimeException("Token already used");

        return resetToken;
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = validatePasswordResetToken(token);
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
