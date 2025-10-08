package com.eryonix.controller;
import com.eryonix.model.Post;
import com.eryonix.model.User;
import com.eryonix.model.Video;
import com.eryonix.exception.ResourceNotFoundException;
import com.eryonix.repository.LikeRepository;
import com.eryonix.repository.PostRepository;
import com.eryonix.repository.UserRepository;
import com.eryonix.repository.VideoRepository;
import com.eryonix.service.LikeService;
import jakarta.annotation.PostConstruct;

import java.security.Principal;
import java.util.Optional;
import jakarta.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
private UserRepository userRepository;
@Autowired
private LikeRepository likeRepository;
@Autowired
private PostRepository postRepository;
@Autowired
private VideoRepository videoRepository;


   @PostMapping("/post/{postId}")
public ResponseEntity<?> likePost(@PathVariable Long postId, Principal principal) {
    System.out.println(" Principal: " + principal);
    String username = principal.getName(); 
    System.out.println("👤 Username from token: " + username);
    User user = userRepository.findByUsername(username)
                 .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    likeService.likePost(postId, user.getId());
    return ResponseEntity.ok("Post liked successfully.");
}

@PostMapping("/video/{videoId}")
public ResponseEntity<?> likeVideo(@PathVariable Long videoId, Principal principal) {
    String username = principal.getName();
    User user = userRepository.findByUsername(username)
                 .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    likeService.likeVideo(videoId, user.getId());
    return ResponseEntity.ok("Video liked successfully.");
}


    @GetMapping("/post/{postId}/count")
    public ResponseEntity<?> getPostLikes(@PathVariable Long postId) {
        long count = likeService.getLikeCountForPost(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/video/{videoId}/count")
    public ResponseEntity<?> getVideoLikes(@PathVariable Long videoId) {
        System.out.println(" getVideoLikes called for videoId = " + videoId);
        long count = likeService.getLikeCountForVideo(videoId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/post/{postId}/liked")
    public ResponseEntity<Boolean> hasLikedPost(@PathVariable Long postId, Principal principal) {
        String username = principal.getName();
        Long userId = userRepository.findByUsername(username)
    .orElseThrow(() -> new RuntimeException("User not found"))
    .getId();
 
        boolean liked = likeRepository.existsByUserIdAndPostId(userId, postId);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/video/{videoId}/liked")
    public ResponseEntity<Boolean> hasLikedVideo(@PathVariable Long videoId, Principal principal) {
        String username = principal.getName();
        Long userId = userRepository.findByUsername(username)
    .orElseThrow(() -> new RuntimeException("User not found"))
    .getId();

        boolean liked = likeRepository.existsByUserIdAndVideoId(userId, videoId);
        return ResponseEntity.ok(liked);
    }

@DeleteMapping("/post/{postId}")
@Transactional
public ResponseEntity<?> unlikePost(@PathVariable Long postId, Principal principal) {
    Optional<User> userOpt = userRepository.findByUsername(principal.getName());
    Optional<Post> postOpt = postRepository.findById(postId);

    if (userOpt.isEmpty() || postOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    likeRepository.deleteByUserAndPost(userOpt.get(), postOpt.get());
    return ResponseEntity.ok().build();
}

@DeleteMapping("/video/{videoId}")
@Transactional
public ResponseEntity<?> unlikeVideo(@PathVariable Long videoId, Principal principal) {
    Optional<User> userOpt = userRepository.findByUsername(principal.getName());
    Optional<Video> videoOpt = videoRepository.findById(videoId);

    if (userOpt.isEmpty() || videoOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    likeRepository.deleteByUserAndVideo(userOpt.get(), videoOpt.get());
    return ResponseEntity.ok().build();
}



    @PostConstruct
    public void init() {
        System.out.println(" LikeController initialized");
    }
}
