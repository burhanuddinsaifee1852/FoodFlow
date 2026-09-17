package com.foodflow.profile.dto;

import java.time.Instant;

public record ProfileResponse(Long id, Long userId, String fullName, String phoneNumber,
                              String address, String city, String postalCode, String profileImageUrl,
                              Instant createdAt, Instant updatedAt) {
}
