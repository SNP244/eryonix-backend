package com.eryonix.controller;




import com.eryonix.model.User;
import com.eryonix.dto.SignupRequest;
import com.eryonix.dto.UserProfileResponse;
import com.eryonix.dto.UserProfileUpdateRequest;
import com.eryonix.repository.UserRepository;
import com.eryonix.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        // check for existing user
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .fullname(signupRequest.getFullname())
                .role("USER")
                .skills(signupRequest.getSkills())
                .portfolioLink(signupRequest.getPortfolioLink())
                .bio(signupRequest.getBio())
                .build();

        // password encryption
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return ResponseEntity.ok(userService.saveUser(user));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .skills(user.getSkills())
                .portfolioLink(user.getPortfolioLink())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateProfile(@RequestBody UserProfileUpdateRequest updateRequest, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only update fields that are provided (not null or empty)
        if (updateRequest.getFullname() != null && !updateRequest.getFullname().trim().isEmpty()) {
            user.setFullname(updateRequest.getFullname());
        }
        if (updateRequest.getSkills() != null && !updateRequest.getSkills().isEmpty()) {
            user.setSkills(updateRequest.getSkills());
        }
        if (updateRequest.getPortfolioLink() != null && !updateRequest.getPortfolioLink().trim().isEmpty()) {
            user.setPortfolioLink(updateRequest.getPortfolioLink());
        }
        if (updateRequest.getBio() != null && !updateRequest.getBio().trim().isEmpty()) {
            user.setBio(updateRequest.getBio());
        }
        if (updateRequest.getProfilePictureUrl() != null && !updateRequest.getProfilePictureUrl().trim().isEmpty()) {
            user.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
        }

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/{username}")
public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
    return userRepository.findByUsername(username)
        .<ResponseEntity<?>>map(user -> {
            UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .skills(user.getSkills())
                .portfolioLink(user.getPortfolioLink())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();

            return ResponseEntity.ok(response);
        })
        .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
}


}
