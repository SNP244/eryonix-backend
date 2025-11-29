package com.eryonix.dto;

import java.time.LocalDateTime;

public record VideoResponse(Long id,String videoUrl,String caption, String category, LocalDateTime createdAt, String username) {}
