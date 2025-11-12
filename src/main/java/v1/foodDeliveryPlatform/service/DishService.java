package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.Dish;

import java.util.List;
import java.util.UUID;

public interface DishService {
    Dish getById(UUID id);

    Dish createDish(Dish dish, UUID restaurantId);

    List<Dish> getAllByRestaurantId(UUID restaurantId);

    Dish updateDish(Dish dish);

    void delete(UUID id);
}
