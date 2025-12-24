package v1.foodDeliveryPlatform.facade;

import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;

import java.util.List;
import java.util.UUID;

public interface ImageFacade {

    List<String> getAllByDishId(UUID dishId);

    DishDto removeImageByDishId(UUID dishId, String image) throws Exception;

    DishDto removeAllImagesByDishId(UUID dishId);

    List<String> getAllByRestaurantId(UUID restaurantId);

    RestaurantDto removeImageByRestaurantId(UUID restaurantId, String image) throws Exception;

    RestaurantDto removeAllImagesByRestaurantId(UUID restaurantId);
}
