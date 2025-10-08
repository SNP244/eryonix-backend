package com.eryonix.controller;

import com.eryonix.model.User;
import com.eryonix.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

   @PostMapping("/{username}")
public ResponseEntity<String> follow(@PathVariable String username, Principal principal) {
    String followerUsername = principal.getName();

    //  Prevent self-follow
    if (followerUsername.equals(username)) {
        return ResponseEntity.badRequest().body("You cannot follow yourself.");
    }

    boolean followed = followService.followUser(followerUsername, username);
    if (followed) {
        return ResponseEntity.ok("Followed " + username);
    } else {
        return ResponseEntity.ok("Already following " + username);
    }
}


    @DeleteMapping("/{username}")
    public ResponseEntity<String> unfollow(@PathVariable String username, Principal principal) {
        String followerUsername = principal.getName();
        boolean unfollowed = followService.unfollowUser(followerUsername, username);
        if (unfollowed) {
            return ResponseEntity.ok("Unfollowed " + username);
        } else {
            return ResponseEntity.ok("Was not following " + username);
        }
    }

    @GetMapping("/followers/{username}")
    public ResponseEntity<List<User>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowers(username));
    }

    @GetMapping("/following/{username}")
    public ResponseEntity<List<User>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowing(username));
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String username, Principal principal) {
        String followerUsername = principal.getName();
        return ResponseEntity.ok(followService.isFollowing(followerUsername, username));
    }
}
