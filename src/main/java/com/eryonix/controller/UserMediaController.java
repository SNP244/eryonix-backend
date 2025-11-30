package com.eryonix.controller;

import com.eryonix.dto.PostResponse;
import com.eryonix.dto.VideoResponse;
import com.eryonix.model.*;
import com.eryonix.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.time.LocalDateTime;

import java.util.List;

import java.io.*;

@RestController
@RequestMapping("/api/media")
public class UserMediaController {

    @Autowired private PostRepository postRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private com.eryonix.service.CloudinaryService cloudinaryService;

    @PostMapping("/upload-post")
    public ResponseEntity<?> uploadPost(@RequestParam("image") MultipartFile image,
                                        @RequestParam("caption") String caption,
                                        @RequestParam(value = "category", required = false) String category, 
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String imageUrl = cloudinaryService.uploadFile(image, "eryonix/posts");

            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

            Post post = new Post();
            post.setImageUrl(imageUrl);
            post.setCaption(caption);
            post.setCategory(category);
            post.setCreatedAt(LocalDateTime.now());
            post.setUser(user);

            postRepository.save(post);

            return ResponseEntity.ok("Post uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image.");
        }
    }

    @PostMapping("/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile video,
                                         @RequestParam("caption") String caption,
                                         @RequestParam(value = "category", required = false) String category, 
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String videoUrl = cloudinaryService.uploadFile(video, "eryonix/videos");

            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

            Video videoEntity = new Video();
            videoEntity.setVideoUrl(videoUrl);
            videoEntity.setCaption(caption);
            videoEntity.setCategory(category);
            videoEntity.setCreatedAt(LocalDateTime.now());
            videoEntity.setUser(user);

            videoRepository.save(videoEntity);

            return ResponseEntity.ok("Video uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload video.");
        }
    }

    @GetMapping("/posts")
public ResponseEntity<List<PostResponse>> getAllPosts() {
    List<PostResponse> posts = postRepository.findAll()
        .stream()
        .map(post -> new PostResponse(
            post.getId(),
            post.getImageUrl(),
            post.getCaption(),
             post.getCategory(), 
            post.getCreatedAt(),
            post.getUser().getUsername()
        ))
        .toList();
    return ResponseEntity.ok(posts);
}

@GetMapping("/videos")
public ResponseEntity<List<VideoResponse>> getAllVideos() {
    List<VideoResponse> videos = videoRepository.findAll()
        .stream()
        .map(video -> new VideoResponse(
            video.getId(),
            video.getVideoUrl(),
            video.getCaption(),
            video.getCategory(),
            video.getCreatedAt(), 
            video.getUser().getUsername()
        ))
        .toList();
    return ResponseEntity.ok(videos);
}

//  Get only the logged-in user's posts
@GetMapping("/my-posts")
public ResponseEntity<List<PostResponse>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<PostResponse> myPosts = postRepository.findByUser(user)
        .stream()
        .map(post -> new PostResponse(
            post.getId(),
            post.getImageUrl(),
            post.getCaption(),
            post.getCategory(),
            post.getCreatedAt(),
            post.getUser().getUsername()
        ))
        .toList();

    return ResponseEntity.ok(myPosts);
}

//  Get only the logged-in user's videos
@GetMapping("/my-videos")
public ResponseEntity<List<VideoResponse>> getMyVideos(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<VideoResponse> myVideos = videoRepository.findByUser(user)
        .stream()
        .map(video -> new VideoResponse(
            video.getId(),
            video.getVideoUrl(),
            video.getCaption(),
            video.getCategory(),
            video.getCreatedAt(),
            video.getUser().getUsername()
        ))
        .toList();

    return ResponseEntity.ok(myVideos);
}


     // === DELETE Post ===
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null || !post.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(401).body("Unauthorized or post not found");
        }
        postRepository.delete(post);
        return ResponseEntity.ok("Post deleted successfully.");
    }

    // === DELETE Video ===
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null || !video.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(401).body("Unauthorized or video not found");
        }
        videoRepository.delete(video);
        return ResponseEntity.ok("Video deleted successfully.");
    }
    
    // Get posts by username (public profile)
@GetMapping("/posts/user/{username}")
public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<PostResponse> posts = postRepository.findByUser(user)
        .stream()
        .map(post -> new PostResponse(
            post.getId(),
            post.getImageUrl(),
            post.getCaption(),
            post.getCategory(),
            post.getCreatedAt(),
            user.getUsername()
        ))
        .toList();

    return ResponseEntity.ok(posts);
}

// Get videos by username (public profile)
@GetMapping("/videos/user/{username}")
public ResponseEntity<List<VideoResponse>> getVideosByUsername(@PathVariable String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<VideoResponse> videos = videoRepository.findByUser(user)
        .stream()
        .map(video -> new VideoResponse(
            video.getId(),
            video.getVideoUrl(),
            video.getCaption(),
            video.getCategory(),
            video.getCreatedAt(),
            user.getUsername()
        ))
        .toList();

    return ResponseEntity.ok(videos);
}

   @GetMapping("/posts/category/{category}")
public ResponseEntity<List<PostResponse>> getPostsByCategory(@PathVariable String category) {
    List<PostResponse> posts = postRepository.findByCategory(category)
        .stream()
        .map(post -> new PostResponse(
            post.getId(),
            post.getImageUrl(),
            post.getCaption(),
            post.getCategory(),
            post.getCreatedAt(),
            post.getUser().getUsername()
        ))
        .toList();

    return ResponseEntity.ok(posts);
}

@GetMapping("/videos/category/{category}")
public ResponseEntity<List<VideoResponse>> getVideosByCategory(@PathVariable String category) {
    List<VideoResponse> videos = videoRepository.findByCategory(category)
        .stream()
        .map(video -> new VideoResponse(
            video.getId(),
            video.getVideoUrl(),
            video.getCaption(),
            video.getCategory(),
            video.getCreatedAt(),
            video.getUser().getUsername()
        ))
        .toList();

    return ResponseEntity.ok(videos);
}

    
    
    }

   


    

    


