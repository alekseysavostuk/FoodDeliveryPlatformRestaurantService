package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.model.DishImage;
import v1.foodDeliveryPlatform.model.feign.DishClient;

import java.util.List;
import java.util.UUID;

public interface DishService {
    Dish getById(UUID id);

    Dish createDish(Dish dish, UUID restaurantId);

    List<Dish> getAllByRestaurantId(UUID restaurantId);

    Dish updateDish(Dish dish);

    void delete(UUID id);

    Dish uploadImage(UUID id, DishImage image);

    boolean existsDish(UUID restaurantId, UUID dishId);

    DishClient getNameById(UUID id);
}
