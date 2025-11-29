package com.eryonix.service;

import com.eryonix.model.*;
import com.eryonix.repository.*;
import com.eryonix.exception.ResourceNotFoundException;
import com.eryonix.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot like your own post.");
        }

        boolean alreadyLiked = likeRepository.existsByUserIdAndPostId(userId, postId);
        if (alreadyLiked) return;

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);
    }

    public void likeVideo(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (video.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot like your own video.");
        }

        boolean alreadyLiked = likeRepository.existsByUserIdAndVideoId(userId, videoId);
        if (alreadyLiked) return;

        Like like = new Like();
        like.setUser(user);
        like.setVideo(video);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);
    }

    public long getLikeCountForPost(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public long getLikeCountForVideo(Long videoId) {
        return likeRepository.countByVideoId(videoId);
    }

    

}
