package com.eryonix.repository;

import com.eryonix.model.Follow;
import com.eryonix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollowing(User user);   // Get followers

    List<Follow> findByFollower(User user);    // Get following
}
