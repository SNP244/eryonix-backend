package com.eryonix.service;


import com.eryonix.model.User;
import com.eryonix.model.Video;
import com.eryonix.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final CloudinaryService cloudinaryService;

    public Video uploadVideo(MultipartFile file, String title, User user) throws IOException {
        // Upload to Cloudinary
        String fileUrl = cloudinaryService.uploadFile(file, "eryonix/videos");

        // Create Video object and save to DB
        Video video = new Video();
    
        video.setUser(user);
        video.setVideoUrl(fileUrl);

        return videoRepository.save(video);
    }

    public List<Video> getVideosByUser(User user) {
        return videoRepository.findByUser(user);
    }
}
