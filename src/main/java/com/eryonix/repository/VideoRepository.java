package com.eryonix.repository;

import com.eryonix.model.User;
import com.eryonix.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByUser(User user); 
    List<Video> findByUserUsername(String username); 
    List<Video> findByCategory(String category);

}

