package com.foodflow.review.dto;

import com.foodflow.review.ReviewTargetType;

import java.time.Instant;

public record ReviewResponse(Long id, Long userId, ReviewTargetType targetType, Long targetId,
                             Integer rating, String comment, Instant createdAt, Instant updatedAt) {
}
