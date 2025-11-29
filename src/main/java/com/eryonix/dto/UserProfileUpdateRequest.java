package com.eryonix.dto;



import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {
    private String fullname;
    private List<String> skills;
    private String portfolioLink;
    private String bio;
    private String profilePictureUrl;  
}
