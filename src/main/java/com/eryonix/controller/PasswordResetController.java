package com.eryonix.controller;

import com.eryonix.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService resetService;

    public PasswordResetController(PasswordResetService resetService) {
        this.resetService = resetService;
    }

    // Send reset link
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            resetService.createPasswordResetToken(email);
            // Mask response to avoid leaking user existence
            return ResponseEntity.ok("If that email is registered, a reset link has been sent.");
        } catch (RuntimeException ex) {
            // Still return 200 for UX/privacy; log if you want
            return ResponseEntity.ok("If that email is registered, a reset link has been sent.");
        }
    }

    // Accept new password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        try {
            resetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
