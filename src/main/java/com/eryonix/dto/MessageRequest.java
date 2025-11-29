package com.eryonix.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private Long receiverId;
    private String content;
    private String sharedMediaUrl;
    private String sharedMediaType;

    
}