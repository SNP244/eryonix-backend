package com.eryonix.controller;

import com.eryonix.model.Comment;
import com.eryonix.dto.CommentRequest;
import com.eryonix.model.Post;
import com.eryonix.model.Video;
import com.eryonix.repository.CommentRepository;
import com.eryonix.repository.PostRepository;
import com.eryonix.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VideoRepository videoRepository;

 @PostMapping
public ResponseEntity<?> addComment(@RequestBody CommentRequest request, Principal principal) {
    Comment comment = new Comment();
    comment.setUsername(principal.getName());
    comment.setContent(request.getContent());
    comment.setMediaType(request.getMediaType().toUpperCase());
    comment.setCreatedAt(LocalDateTime.now());

    if (request.getMediaType().equalsIgnoreCase("post")) {
        Post post = postRepository.findById(request.getMediaId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);
    } else if (request.getMediaType().equalsIgnoreCase("video")) {
        Video video = videoRepository.findById(request.getMediaId())
                .orElseThrow(() -> new RuntimeException("Video not found"));
        comment.setVideo(video);
    } else {
        return ResponseEntity.badRequest().body("Invalid media type");
    }

    Comment saved = commentRepository.save(comment);
    return ResponseEntity.ok(saved);
}

    @GetMapping("/{mediaType}/{mediaId}")
    public ResponseEntity<List<Comment>> getComments(
            @PathVariable String mediaType,
            @PathVariable Long mediaId) {

        List<Comment> comments;

        if (mediaType.equalsIgnoreCase("post")) {
            Post post = postRepository.findById(mediaId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        } else if (mediaType.equalsIgnoreCase("video")) {
            Video video = videoRepository.findById(mediaId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));
            comments = commentRepository.findByVideoOrderByCreatedAtAsc(video);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<Comment> replyToComment(@PathVariable Long parentId,
                                                  @RequestBody Comment reply,
                                                  Principal principal) {
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        reply.setParent(parent);
        reply.setUsername(principal.getName());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setPost(parent.getPost());
        reply.setVideo(parent.getVideo());
        reply.setMediaType(parent.getMediaType());

        Comment saved = commentRepository.save(reply);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long parentId) {
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getCommentCountForPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        long count = commentRepository.countByPost(post);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/video/{videoId}/count")
    public ResponseEntity<Long> getCommentCountForVideo(@PathVariable Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        long count = commentRepository.countByVideo(video);
        return ResponseEntity.ok(count);
    }
}
