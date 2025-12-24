package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.model.Restaurant;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    Dish removeImageByDishId(UUID dishId, String image) throws Exception;

    Dish removeAllImagesByDishId(UUID dishId);

    List<String> getAllByDishId(UUID dishId);

    Restaurant removeImageByRestaurantId(UUID restaurantId, String image) throws Exception;

    Restaurant removeAllImagesByRestaurantId(UUID restaurantId);

    List<String> getAllByRestaurantId(UUID restaurantId);
}
