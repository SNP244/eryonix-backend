package com.eryonix.service;


import com.eryonix.model.Post;
import com.eryonix.model.User;
import com.eryonix.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;

    public Post createPost(MultipartFile file, String caption, User user) throws IOException {
        // Upload to Cloudinary
        String imageUrl = cloudinaryService.uploadFile(file, "eryonix/posts");

        // Create Post and save to DB
        Post post = new Post();
        post.setCaption(caption);
        post.setImageUrl(imageUrl);
        post.setUser(user);

        return postRepository.save(post);
    }

    public List<Post> getPostsByUsername(String username) {
        return postRepository.findByUserUsername(username);
    }
}
