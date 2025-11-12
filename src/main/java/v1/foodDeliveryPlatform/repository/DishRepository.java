package v1.foodDeliveryPlatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import v1.foodDeliveryPlatform.model.Dish;

import java.util.List;
import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {

    @Query(value = "SELECT * FROM dish WHERE restaurant_id = :restaurantId", nativeQuery = true)
    List<Dish> findAllByRestaurantId(@Param("restaurantId") UUID restaurantId);

    @Modifying
    @Query(value = "DELETE FROM dish WHERE id = :id", nativeQuery = true)
    void deleteDirectlyById(@Param("id") UUID id);

    @Modifying
    @Query(value = "DELETE FROM dish_images WHERE dish_id = :dishId", nativeQuery = true)
    void deleteImagesByDishId(@Param("dishId") UUID dishId);
}
