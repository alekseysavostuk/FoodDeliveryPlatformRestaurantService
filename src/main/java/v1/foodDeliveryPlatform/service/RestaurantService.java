package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.Restaurant;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {

    Restaurant getById(UUID id);

    Restaurant createRestaurant(Restaurant restaurant);

    List<Restaurant> getAllRestaurants();

    Restaurant updateRestaurant(Restaurant restaurant);

    void delete(UUID id);

    List<Restaurant> getAllByCuisine(String cuisine);
}
