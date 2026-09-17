package com.foodflow.review;

import com.foodflow.review.dto.ReviewRequest;
import com.foodflow.review.dto.ReviewResponse;
import com.foodflow.review.dto.ReviewSummaryResponse;
import com.foodflow.review.dto.ReviewUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReviewResponse> list(@RequestParam ReviewTargetType targetType,
                                     @RequestParam @Positive Long targetId) {
        return service.list(targetType, targetId);
    }

    @GetMapping("/summary")
    public ReviewSummaryResponse summary(@RequestParam ReviewTargetType targetType,
                                         @RequestParam @Positive Long targetId) {
        return service.summary(targetType, targetId);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{reviewId}")
    public ReviewResponse update(@PathVariable @Positive Long reviewId,
                                 @Valid @RequestBody ReviewUpdateRequest request) {
        return service.update(reviewId, request);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long reviewId,
                                       @RequestParam @Positive Long userId) {
        service.delete(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
