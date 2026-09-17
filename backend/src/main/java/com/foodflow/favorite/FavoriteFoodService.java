package com.foodflow.favorite;

import com.foodflow.common.exception.DuplicateResourceException;
import com.foodflow.common.exception.ResourceNotFoundException;
import com.foodflow.favorite.dto.FavoriteFoodResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FavoriteFoodService {
    private final FavoriteFoodRepository repository;

    public FavoriteFoodService(FavoriteFoodRepository repository) {
        this.repository = repository;
    }

    public List<FavoriteFoodResponse> list(Long userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public FavoriteFoodResponse add(Long userId, Long foodId) {
        if (repository.existsByUserIdAndFoodId(userId, foodId)) {
            throw new DuplicateResourceException("Food " + foodId + " is already in this user's favorites.");
        }
        FavoriteFood favorite = new FavoriteFood();
        favorite.setUserId(userId);
        favorite.setFoodId(foodId);
        return toResponse(repository.save(favorite));
    }

    @Transactional
    public void remove(Long userId, Long foodId) {
        FavoriteFood favorite = repository.findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite food was not found."));
        repository.delete(favorite);
    }

    private FavoriteFoodResponse toResponse(FavoriteFood favorite) {
        return new FavoriteFoodResponse(favorite.getId(), favorite.getUserId(), favorite.getFoodId(), favorite.getCreatedAt());
    }
}
