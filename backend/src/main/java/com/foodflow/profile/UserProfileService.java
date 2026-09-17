package com.foodflow.profile;

import com.foodflow.common.exception.DuplicateResourceException;
import com.foodflow.common.exception.ResourceNotFoundException;
import com.foodflow.profile.dto.ProfileRequest;
import com.foodflow.profile.dto.ProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserProfileService {
    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public ProfileResponse getByUserId(Long userId) {
        return toResponse(findByUserId(userId));
    }

    @Transactional
    public ProfileResponse create(Long userId, ProfileRequest request) {
        if (repository.existsByUserId(userId)) {
            throw new DuplicateResourceException("A profile already exists for user " + userId + ".");
        }
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        copy(request, profile);
        return toResponse(repository.save(profile));
    }

    @Transactional
    public ProfileResponse update(Long userId, ProfileRequest request) {
        UserProfile profile = findByUserId(userId);
        copy(request, profile);
        return toResponse(repository.save(profile));
    }

    private UserProfile findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user " + userId + "."));
    }

    private void copy(ProfileRequest request, UserProfile profile) {
        profile.setFullName(request.fullName().trim());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setAddress(request.address());
        profile.setCity(request.city());
        profile.setPostalCode(request.postalCode());
        profile.setProfileImageUrl(request.profileImageUrl());
    }

    private ProfileResponse toResponse(UserProfile profile) {
        return new ProfileResponse(profile.getId(), profile.getUserId(), profile.getFullName(),
                profile.getPhoneNumber(), profile.getAddress(), profile.getCity(), profile.getPostalCode(),
                profile.getProfileImageUrl(), profile.getCreatedAt(), profile.getUpdatedAt());
    }
}
