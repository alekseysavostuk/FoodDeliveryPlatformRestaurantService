package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.Dish;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    Dish removeImageByDishId(UUID dishId, String image) throws Exception;

    Dish removeAllImagesByDishId(UUID dishId);

    List<String> getAllByDishId(UUID dishId);
}
