package com.eryonix.repository;

import com.eryonix.model.Comment;
import com.eryonix.model.Post;
import com.eryonix.model.Video;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
List<Comment> findByVideoOrderByCreatedAtAsc(Video video);

    
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);  

    long countByPost(Post post);
    long countByVideo(Video video);


}

