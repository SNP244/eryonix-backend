package com.eryonix.dto;

import java.time.LocalDateTime;

public record PostResponse(Long id,String imageUrl, String caption, String category, LocalDateTime createdAt, String username) {}
