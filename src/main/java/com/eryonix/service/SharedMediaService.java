package com.eryonix.service;
import com.eryonix.repository.PostRepository;
import com.eryonix.repository.VideoRepository;
import org.springframework.http.ResponseEntity;
import com.eryonix.dto.SharedMediaResponse;


import com.eryonix.model.SharedMedia;
import com.eryonix.model.User;
import com.eryonix.dto.SharedMediaResponse.CommentDTO;
import com.eryonix.model.Message;
import com.eryonix.repository.SharedMediaRepository;
import com.eryonix.repository.UserRepository;
import com.eryonix.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class SharedMediaService {

    @Autowired
private PostRepository postRepository;

@Autowired
private VideoRepository videoRepository;

@Autowired
private MessageRepository messageRepository;



    @Autowired
    private SharedMediaRepository sharedMediaRepository;

    @Autowired
    private UserRepository userRepository;

    public void shareMedia(String senderUsername, List<String> recipientUsernames, String message, Long mediaId, SharedMedia.MediaType mediaType) {
        User sender = userRepository.findByUsername(senderUsername).orElseThrow(() -> new RuntimeException("Sender not found"));

        for (String recipientUsername : recipientUsernames) {
            User recipient = userRepository.findByUsername(recipientUsername).orElseThrow(() -> new RuntimeException("Recipient not found"));

            SharedMedia shared = new SharedMedia();
            shared.setSender(sender);
            shared.setRecipient(recipient);
            shared.setMessage(message);
            shared.setMediaId(mediaId);
            shared.setMediaType(mediaType);
            shared.setTimestamp(LocalDateTime.now());

            sharedMediaRepository.save(shared);

//  Also save to message table for chat
Message mediaMessage = new Message();
mediaMessage.setSender(sender);
mediaMessage.setReceiver(recipient);
mediaMessage.setContent(String.valueOf(mediaId)); // Message body is just the ID
mediaMessage.setSharedMediaUrl(String.valueOf(mediaId)); 
mediaMessage.setSharedMediaType(mediaType.name().toLowerCase()); // e.g. "post"
mediaMessage.setTimestamp(LocalDateTime.now());

messageRepository.save(mediaMessage);

// Optional: send typed message separately
if (message != null && !message.isBlank()) {
    Message textMessage = new Message();
    textMessage.setSender(sender);
    textMessage.setReceiver(recipient);
    textMessage.setContent(message);
    textMessage.setTimestamp(LocalDateTime.now());
    messageRepository.save(textMessage);
}

        }
    }


    // ---------- NEW: helper mappers ----------
    private List<CommentDTO> mapPostComments(List<com.eryonix.model.Comment> comments) {
        if (comments == null) return List.of();
        return comments.stream().map(c -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(c.getId());
            dto.setUsername(c.getUsername());
            dto.setContent(c.getContent());
            dto.setCreatedAt(c.getCreatedAt()); 
            return dto;
        }).toList();
    }

    private List<CommentDTO> mapVideoComments(List<com.eryonix.model.Comment> comments) {
        if (comments == null) return List.of();
        return comments.stream().map(c -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(c.getId());
            dto.setUsername(c.getUsername());
            dto.setContent(c.getContent());
            dto.setCreatedAt(c.getCreatedAt()); 
            return dto;
        }).toList();
    }

    // ---------- UPDATED: returns SharedMediaResponse DTO ----------
    public ResponseEntity<?> getSharedPost(Long postId) {
        return postRepository.findById(postId)
            .map(post -> {
                SharedMediaResponse dto = new SharedMediaResponse();
                dto.setId(post.getId());
                dto.setCaption(post.getCaption());
                dto.setMediaUrl(post.getImageUrl()); // unified field
                dto.setUsername(post.getUser().getUsername());
                dto.setMediaType("POST");

                int likes = post.getLikes() != null ? post.getLikes().size() : 0;
                dto.setLikesCount(likes);

                var comments = post.getComments();
                dto.setCommentsCount(comments != null ? comments.size() : 0);
                dto.setComments(mapPostComments(comments));

                int shares = sharedMediaRepository.countByMediaTypeAndMediaId(SharedMedia.MediaType.POST, post.getId());
                dto.setSharesCount(shares);

                return ResponseEntity.ok(dto);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());

    }

    public ResponseEntity<?> getSharedVideo(Long videoId) {
        return videoRepository.findById(videoId)
            .map(video -> {
                SharedMediaResponse dto = new SharedMediaResponse();
                dto.setId(video.getId());
                dto.setCaption(video.getCaption());
                dto.setMediaUrl(video.getVideoUrl()); // unified field
                dto.setUsername(video.getUser().getUsername());
                dto.setMediaType("VIDEO");

                int likes = video.getLikes() != null ? video.getLikes().size() : 0;
                dto.setLikesCount(likes);

                var comments = video.getComments();
                dto.setCommentsCount(comments != null ? comments.size() : 0);
                dto.setComments(mapVideoComments(comments));

                int shares = sharedMediaRepository.countByMediaTypeAndMediaId(SharedMedia.MediaType.VIDEO, video.getId());
                dto.setSharesCount(shares);

                return ResponseEntity.ok(dto);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());

    }

    // ---------- OPTIONAL: when you have a shareId and want to enrich with share metadata ----------
    public ResponseEntity<?> getSharedMediaWithMeta(Long shareId) {
        return sharedMediaRepository.findById(shareId)
            .map(shared -> {
                if (shared.getMediaType() == SharedMedia.MediaType.POST) {
                    ResponseEntity<?> resp = getSharedPost(shared.getMediaId());
                    if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() instanceof SharedMediaResponse dto) {
                        dto.setSharedBy(shared.getSender().getUsername());
                        dto.setShareMessage(shared.getMessage());
                        dto.setSharedAt(shared.getTimestamp());
                        return ResponseEntity.ok(dto);
                    }
                    return resp;
                } else {
                    ResponseEntity<?> resp = getSharedVideo(shared.getMediaId());
                    if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() instanceof SharedMediaResponse dto) {
                        dto.setSharedBy(shared.getSender().getUsername());
                        dto.setShareMessage(shared.getMessage());
                        dto.setSharedAt(shared.getTimestamp());
                        return ResponseEntity.ok(dto);
                    }
                    return resp;
                }
            })
            .orElseGet(() -> ResponseEntity.notFound().build());

    }

}
