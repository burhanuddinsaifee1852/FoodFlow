package com.foodflow.review;

import com.foodflow.common.exception.DuplicateResourceException;
import com.foodflow.common.exception.ResourceNotFoundException;
import com.foodflow.review.dto.ReviewRequest;
import com.foodflow.review.dto.ReviewResponse;
import com.foodflow.review.dto.ReviewSummaryResponse;
import com.foodflow.review.dto.ReviewUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public List<ReviewResponse> list(ReviewTargetType targetType, Long targetId) {
        return findTargetReviews(targetType, targetId).stream().map(this::toResponse).toList();
    }

    public ReviewSummaryResponse summary(ReviewTargetType targetType, Long targetId) {
        List<Review> reviews = findTargetReviews(targetType, targetId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return new ReviewSummaryResponse(targetType, targetId, reviews.size(), Math.round(average * 100.0) / 100.0);
    }

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        if (repository.findByUserIdAndTargetTypeAndTargetId(request.userId(), request.targetType(), request.targetId()).isPresent()) {
            throw new DuplicateResourceException("A user may submit only one review for the same food or restaurant.");
        }
        Review review = new Review();
        review.setUserId(request.userId());
        review.setTargetType(request.targetType());
        review.setTargetId(request.targetId());
        copy(request.rating(), request.comment(), review);
        return toResponse(repository.save(review));
    }

    @Transactional
    public ReviewResponse update(Long reviewId, ReviewUpdateRequest request) {
        Review review = findById(reviewId);
        requireOwner(review, request.userId());
        copy(request.rating(), request.comment(), review);
        return toResponse(repository.save(review));
    }

    @Transactional
    public void delete(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        requireOwner(review, userId);
        repository.delete(review);
    }

    private List<Review> findTargetReviews(ReviewTargetType targetType, Long targetId) {
        return repository.findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId);
    }

    private Review findById(Long reviewId) {
        return repository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review " + reviewId + " was not found."));
    }

    private void requireOwner(Review review, Long userId) {
        if (!review.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Review not found for this user.");
        }
    }

    private void copy(Integer rating, String comment, Review review) {
        review.setRating(rating);
        review.setComment(comment == null ? null : comment.trim());
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(review.getId(), review.getUserId(), review.getTargetType(), review.getTargetId(),
                review.getRating(), review.getComment(), review.getCreatedAt(), review.getUpdatedAt());
    }
}
