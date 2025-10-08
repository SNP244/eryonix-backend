package com.eryonix.model;



import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role; // USER, FREELANCER, ADMIN

    // Freelancer-specific fields
    @ElementCollection
    private List<String> skills; 
    private String portfolioLink;  
    private String bio;  

    // New field for profile picture URL
    private String profilePictureUrl;  // URL to the freelancer's profile picture

    
}
