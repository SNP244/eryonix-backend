package com.eryonix.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SharedMediaResponse {
    private Long id;           
    private String caption;
    private String mediaUrl;   
    private String username;   
    private String mediaType;  

    private int likesCount;
    private int commentsCount;
    private List<CommentDTO> comments;
    private int sharesCount;

    private String sharedBy;
    private String shareMessage;
    private LocalDateTime sharedAt;

    @Data
    public static class CommentDTO {
        private Long id;
        private String username;
        private String content;
        private LocalDateTime createdAt;
    }
}
