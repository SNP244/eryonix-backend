package com.eryonix.controller;

import com.eryonix.dto.ShareMediaRequest;
import com.eryonix.service.SharedMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
public class SharedMediaController {

    @Autowired
    private SharedMediaService sharedMediaService;

    @PostMapping
    public ResponseEntity<String> shareMedia(@RequestBody ShareMediaRequest request, Authentication auth) {
        String senderUsername = auth.getName();
        sharedMediaService.shareMedia(
                senderUsername,
                request.getRecipients(),
                request.getMessage(),
                request.getMediaId(),
                request.getMediaType()   
        );
        return ResponseEntity.ok("Shared successfully");
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> getSharedMedia(@PathVariable String type, @PathVariable Long id) {
        if (type.equalsIgnoreCase("post")) {
            return sharedMediaService.getSharedPost(id);
        } else if (type.equalsIgnoreCase("video")) {
            return sharedMediaService.getSharedVideo(id);
        } else {
            return ResponseEntity.badRequest().body("Invalid media type");
        }
    }
}
