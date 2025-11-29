package com.eryonix.repository;

import com.eryonix.model.SharedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedMediaRepository extends JpaRepository<SharedMedia, Long> {
    
    int countByMediaTypeAndMediaId(SharedMedia.MediaType mediaType, Long mediaId);
}
