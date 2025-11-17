package v1.foodDeliveryPlatform.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v1.foodDeliveryPlatform.exception.ModelExistsException;
import v1.foodDeliveryPlatform.model.Restaurant;
import v1.foodDeliveryPlatform.repository.RestaurantRepository;
import v1.foodDeliveryPlatform.service.RestaurantService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant getById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new ModelExistsException("Restaurant not found"));
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    @Transactional
    public List<Restaurant> getAllByCuisine(String cuisine) {
        return restaurantRepository.findAllByCuisine(cuisine);
    }

    @Override
    public boolean existsRestaurant(UUID id) {
        return restaurantRepository.findById(id).isPresent();
    }

    @Override
    public Restaurant updateRestaurant(Restaurant restaurant) {
        Restaurant currentRestaurant = getById(restaurant.getId());
        currentRestaurant.setName(restaurant.getName());
        currentRestaurant.setAddress(restaurant.getAddress());
        currentRestaurant.setCuisine(restaurant.getCuisine());
        restaurantRepository.save(currentRestaurant);
        return currentRestaurant;
    }

    @Override
    public void delete(UUID id) {
        restaurantRepository.deleteById(id);
    }


}
