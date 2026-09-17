package com.foodflow.favorite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FavoriteFoodRequest(@NotNull @Positive Long foodId) {
}
