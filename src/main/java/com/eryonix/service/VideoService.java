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

    public Video uploadVideo(MultipartFile file, String title, User user) throws IOException {
        // Generate a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Define upload directory (relative to project root)
        String uploadDir = "uploads/videos/";
        File destinationFile = new File(uploadDir + fileName);

        // Create directories if they don't exist
        destinationFile.getParentFile().mkdirs();

        // Save file to disk
        file.transferTo(destinationFile);

        // Save file URL in DB
        String fileUrl = "/" + uploadDir + fileName;

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
