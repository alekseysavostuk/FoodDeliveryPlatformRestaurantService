package v1.foodDeliveryPlatform.facade.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;
import v1.foodDeliveryPlatform.facade.ImageFacade;
import v1.foodDeliveryPlatform.mapper.DishMapper;
import v1.foodDeliveryPlatform.mapper.RestaurantMapper;
import v1.foodDeliveryPlatform.service.ImageService;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ImageFacadeImpl implements ImageFacade {

    private final ImageService imageService;
    private final DishMapper dishMapper;
    private final RestaurantMapper restaurantMapper;


    @Override
    public List<String> getAllByDishId(UUID dishId) {
        return imageService.getAllByDishId(dishId);
    }

    @Override
    public DishDto removeImageByDishId(UUID dishId, String image) throws Exception {
        return dishMapper.toDto(imageService.removeImageByDishId(dishId, image));
    }

    @Override
    public DishDto removeAllImagesByDishId(UUID dishId) {
        return dishMapper.toDto(imageService.removeAllImagesByDishId(dishId));
    }

    @Override
    public List<String> getAllByRestaurantId(UUID restaurantId) {
        return imageService.getAllByRestaurantId(restaurantId);
    }

    @Override
    public RestaurantDto removeImageByRestaurantId(UUID restaurantId, String image) throws Exception {
        return restaurantMapper.toDto(imageService.removeImageByRestaurantId(restaurantId, image));
    }

    @Override
    public RestaurantDto removeAllImagesByRestaurantId(UUID restaurantId) {
        return restaurantMapper.toDto(imageService.removeAllImagesByRestaurantId(restaurantId));
    }

}
