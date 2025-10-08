package com.eryonix.dto;

import lombok.Data;
import java.util.List;
import com.eryonix.model.SharedMedia.MediaType;

@Data
public class ShareMediaRequest {
    private List<String> recipients;
    private String message;
    private Long mediaId;
    private MediaType mediaType;  
}
