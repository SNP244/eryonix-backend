package com.eryonix.dto;




import lombok.Builder;
import lombok.Data;
import java.util.List;


@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String role;
    private List<String>  skills;
    private String portfolioLink;
    private String bio;
    private String profilePictureUrl;
}
