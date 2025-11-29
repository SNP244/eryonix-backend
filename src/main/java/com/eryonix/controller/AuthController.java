package com.eryonix.controller;


import com.eryonix.dto.LoginRequest;
import com.eryonix.dto.LoginResponse;
import com.eryonix.dto.SignupRequest;
import com.eryonix.dto.UserProfileResponse;
import com.eryonix.dto.UserProfileUpdateRequest;
import com.eryonix.model.User;
import com.eryonix.repository.UserRepository;
import com.eryonix.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.io.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> register(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LoginResponse("Email is already in use.", null,null));
        }

        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LoginResponse("Username is already taken.", null, null));
        }

        User user = new User();
        user.setFullname(signupRequest.getFullname());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole() != null ? signupRequest.getRole() : "USER");

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse("User registered successfully!", null,null));
    }

    @PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    System.out.println("Login attempt for username: " + request.getUsername());

    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        System.out.println("Login successful for username: " + request.getUsername());
        return ResponseEntity.ok(new LoginResponse("Login successful!", token, request.getUsername())); 
    } catch (Exception e) {
        System.out.println("Login failed for username: " + request.getUsername());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse("Invalid credentials.", null, null));
    }
}


    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestBody UserProfileUpdateRequest updateRequest) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullname(updateRequest.getFullname());
        user.setSkills(updateRequest.getSkills());
        user.setPortfolioLink(updateRequest.getPortfolioLink());
        user.setBio(updateRequest.getBio());

        userRepository.save(user);

        return ResponseEntity.ok(UserProfileResponse.builder()
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .skills(user.getSkills())
                .portfolioLink(user.getPortfolioLink())
                .bio(user.getBio())
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(UserProfileResponse.builder()
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .skills(user.getSkills())
                .portfolioLink(user.getPortfolioLink())
                .bio(user.getBio())
                .build());
    }

    @PostMapping("/profile-picture")
public ResponseEntity<String> uploadProfilePicture(@RequestParam("image") MultipartFile image,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
    if (image.isEmpty()) {
        return ResponseEntity.badRequest().body("No file uploaded.");
    }

    User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    try {
        String uploadDir = System.getProperty("user.dir") + "/uploads/profiles/"; 

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        String filePath = uploadDir + filename;

        File destination = new File(filePath);
        image.transferTo(destination); 

        String imageUrl = "/uploads/profiles/" + filename;
user.setProfilePictureUrl(imageUrl);

        userRepository.save(user);

        return ResponseEntity.ok("Profile picture uploaded successfully.");
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Failed to upload profile picture.");
    }
}


}
