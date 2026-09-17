package com.foodflow.favorite.dto;

import java.time.Instant;

public record FavoriteFoodResponse(Long id, Long userId, Long foodId, Instant createdAt) {
}
