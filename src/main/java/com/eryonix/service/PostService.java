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

    public Post createPost(MultipartFile file, String caption, User user) throws IOException {
        // Generate a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Define upload directory (relative to project root)
        String uploadDir = "uploads/images/";
        File destinationFile = new File(uploadDir + fileName);

        // Create directories if they don't exist
        destinationFile.getParentFile().mkdirs();

        // Save image to disk
        file.transferTo(destinationFile);

        // Construct URL
        String imageUrl = "/" + uploadDir + fileName;

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
