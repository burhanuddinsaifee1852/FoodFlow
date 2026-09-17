package com.foodflow.review.dto;

import com.foodflow.review.ReviewTargetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull @Positive Long userId,
        @NotNull ReviewTargetType targetType,
        @NotNull @Positive Long targetId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @Size(max = 1000) String comment) {
}
