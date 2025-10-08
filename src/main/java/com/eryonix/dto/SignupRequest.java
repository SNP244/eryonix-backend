package com.eryonix.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank(message = "Full name is required")
    private String fullname;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private List<String> skills;
    private String portfolioLink;
    private String bio;
    private String role;

public String getRole() {
    return role;
}

public void setRole(String role) {
    this.role = role;
}

}
