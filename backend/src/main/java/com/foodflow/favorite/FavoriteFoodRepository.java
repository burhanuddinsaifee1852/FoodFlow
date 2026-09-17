package com.foodflow.favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, Long> {
    List<FavoriteFood> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteFood> findByUserIdAndFoodId(Long userId, Long foodId);
    boolean existsByUserIdAndFoodId(Long userId, Long foodId);
}
