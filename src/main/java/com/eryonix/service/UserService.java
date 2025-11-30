package com.eryonix.service;

import com.eryonix.dto.SignupRequest;
import com.eryonix.model.User;
import com.eryonix.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper,
                       CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
    }

    private final CloudinaryService cloudinaryService;

    // Register and save a new user using SignupRequest
    public User saveUser(SignupRequest signupRequest) {
        // Convert DTO to Entity using ModelMapper
        User user = modelMapper.map(signupRequest, User.class);

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    // Save user directly using User entity
    public User saveUser(User user) {
        // Encrypt password if present and not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }
    public User updateUserProfile(String username, String fullname, String bio, String portfolioLink, List<String> skills) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setFullname(fullname);
    user.setBio(bio);
    user.setPortfolioLink(portfolioLink);
    user.setSkills(skills); // assuming skills is a List<String> field in User entity

    return userRepository.save(user);
}


    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Upload and save profile picture
    public String uploadProfilePicture(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String fileUrl = cloudinaryService.uploadFile(file, "eryonix/profiles");
        user.setProfilePictureUrl(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }
}
