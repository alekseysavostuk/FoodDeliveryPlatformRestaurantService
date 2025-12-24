package v1.foodDeliveryPlatform.facade;

import v1.foodDeliveryPlatform.dto.minio.ModelImageDto;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;
import v1.foodDeliveryPlatform.dto.model.feign.RestaurantClientDto;

import java.util.List;
import java.util.UUID;

public interface RestaurantFacade {

    RestaurantDto getById(UUID id);

    RestaurantDto createRestaurant(RestaurantDto restaurantDto);

    List<RestaurantDto> getAllRestaurants();

    RestaurantDto updateRestaurant(RestaurantDto restaurantDto);

    void delete(UUID id);

    List<RestaurantDto> getAllByCuisine(String cuisine);

    boolean existsRestaurant(UUID id);

    RestaurantClientDto getNameById(UUID id);

    RestaurantDto uploadImage(UUID id, ModelImageDto image);
}
