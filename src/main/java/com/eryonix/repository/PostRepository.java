package com.eryonix.repository;



import com.eryonix.model.User;
import com.eryonix.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user); 
    List<Post> findByUserUsername(String username); 
    List<Post> findByCategory(String category);
}
