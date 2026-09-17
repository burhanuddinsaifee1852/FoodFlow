package com.foodflow.review.dto;

import com.foodflow.review.ReviewTargetType;

public record ReviewSummaryResponse(ReviewTargetType targetType, Long targetId, long reviewCount,
                                    double averageRating) {
}
