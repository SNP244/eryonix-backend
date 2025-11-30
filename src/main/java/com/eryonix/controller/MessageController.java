package com.eryonix.controller;

import com.eryonix.model.Message;
import com.eryonix.service.MessageService;
import com.eryonix.security.JwtUtil;
import com.eryonix.dto.MessageRequest;
import com.eryonix.model.User;
import com.eryonix.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;


import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.eryonix.service.CloudinaryService cloudinaryService;

    //  Helper method to extract userId from request
    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    //  Get all conversations sorted by latest message (chat list)
    @GetMapping("/conversations")
    public List<Message> getRecentConversations(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return messageService.getRecentChats(userId);
    }

    //  Get full chat between current user and another user
    @GetMapping("/{otherUserId}")
    public List<Message> getConversation(@PathVariable Long otherUserId, HttpServletRequest request) {
        Long userId = extractUserId(request);
        return messageService.getConversation(userId, otherUserId);
    }

    //  Send a new message
    @PostMapping("/send")
public ResponseEntity<?> sendMessage(@RequestBody MessageRequest req, HttpServletRequest request) {
    Long senderId = extractUserId(request);

    String content = req.getContent();

    // If user is sharing a post or video, store only the ID as content
    if (req.getSharedMediaType() != null &&
        (req.getSharedMediaType().equalsIgnoreCase("post") || req.getSharedMediaType().equalsIgnoreCase("video"))) {
        content = req.getContent(); // just the media ID like "12"
    }

    Message msg = messageService.sendMessage(
        senderId,
        req.getReceiverId(),
        content,
        req.getSharedMediaUrl(),
        req.getSharedMediaType()
    );

    return ResponseEntity.ok(msg);
}


    @PostMapping("/upload")
    public ResponseEntity<?> uploadChatMedia(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String fileUrl = cloudinaryService.uploadFile(file, "eryonix/chat");

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            }

            String mediaType = switch (extension.toLowerCase()) {
                case "jpg", "jpeg", "png", "gif", "webp" -> "image";
                case "mp4", "mov", "avi", "mkv" -> "video";
                case "mp3", "wav", "ogg" -> "audio";
                default -> "file";
            };

            //  Only return media info
            return ResponseEntity.ok(Map.of(
                "mediaUrl", fileUrl,
                "mediaType", mediaType
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file.");
        }
    }


}
