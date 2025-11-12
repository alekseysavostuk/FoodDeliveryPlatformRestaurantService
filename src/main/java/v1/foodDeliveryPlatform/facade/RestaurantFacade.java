package v1.foodDeliveryPlatform.facade;

import v1.foodDeliveryPlatform.dto.model.RestaurantDto;

import java.util.List;
import java.util.UUID;

public interface RestaurantFacade {

    RestaurantDto getById(UUID id);

    RestaurantDto createRestaurant(RestaurantDto restaurantDto);

    List<RestaurantDto> getAllRestaurants();

    RestaurantDto updateRestaurant(RestaurantDto restaurantDto);

    void delete(UUID id);

    List<RestaurantDto> getAllByCuisine(String cuisine);
}
