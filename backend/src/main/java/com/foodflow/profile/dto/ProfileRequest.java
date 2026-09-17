package com.foodflow.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank @Size(max = 100) String fullName,
        @Pattern(regexp = "^[+0-9() -]{7,20}$", message = "phoneNumber must be a valid phone number") String phoneNumber,
        @Size(max = 255) String address,
        @Size(max = 100) String city,
        @Size(max = 20) String postalCode,
        @Size(max = 500) String profileImageUrl) {
}
