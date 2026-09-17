package com.foodflow.favorite;

import com.foodflow.favorite.dto.FavoriteFoodRequest;
import com.foodflow.favorite.dto.FavoriteFoodResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/users/{userId}/favorites")
public class FavoriteFoodController {
    private final FavoriteFoodService service;

    public FavoriteFoodController(FavoriteFoodService service) {
        this.service = service;
    }

    @GetMapping
    public List<FavoriteFoodResponse> list(@PathVariable @Positive Long userId) {
        return service.list(userId);
    }

    @PostMapping
    public ResponseEntity<FavoriteFoodResponse> add(@PathVariable @Positive Long userId,
                                                    @Valid @RequestBody FavoriteFoodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(userId, request.foodId()));
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<Void> remove(@PathVariable @Positive Long userId,
                                       @PathVariable @Positive Long foodId) {
        service.remove(userId, foodId);
        return ResponseEntity.noContent().build();
    }
}
