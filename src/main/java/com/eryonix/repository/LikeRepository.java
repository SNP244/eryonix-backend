package com.eryonix.repository;



import com.eryonix.model.Like;
import com.eryonix.model.Post;
import com.eryonix.model.User;
import com.eryonix.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    //  Check if user already liked a specific post
    Optional<Like> findByUserAndPost(User user, Post post);

    //  Check if user already liked a specific video
    Optional<Like> findByUserAndVideo(User user, Video video);

    //  Count likes on a post
    Long countByPost(Post post);

    //  Count likes on a video
    Long countByVideo(Video video);

    //  Delete like by user and post
    void deleteByUserAndPost(User user, Post post);

    //  Delete like by user and video
    void deleteByUserAndVideo(User user, Video video);

    boolean existsByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);

    long countByPostId(Long postId);
    long countByVideoId(Long videoId);
    
}

