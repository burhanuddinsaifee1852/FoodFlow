package com.foodflow.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(ReviewTargetType targetType, Long targetId);
    Optional<Review> findByUserIdAndTargetTypeAndTargetId(Long userId, ReviewTargetType targetType, Long targetId);
}
