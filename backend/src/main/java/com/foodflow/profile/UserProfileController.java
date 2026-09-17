package com.foodflow.profile;

import com.foodflow.profile.dto.ProfileRequest;
import com.foodflow.profile.dto.ProfileResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/users/{userId}/profile")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ProfileResponse get(@PathVariable @Positive Long userId) {
        return service.getByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@PathVariable @Positive Long userId,
                                                  @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, request));
    }

    @PutMapping
    public ProfileResponse update(@PathVariable @Positive Long userId,
                                  @Valid @RequestBody ProfileRequest request) {
        return service.update(userId, request);
    }
}
