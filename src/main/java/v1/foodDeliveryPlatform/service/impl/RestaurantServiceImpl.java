package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import v1.foodDeliveryPlatform.exception.ResourceNotFoundException;
import v1.foodDeliveryPlatform.model.Restaurant;
import v1.foodDeliveryPlatform.model.feign.RestaurantClient;
import v1.foodDeliveryPlatform.repository.RestaurantRepository;
import v1.foodDeliveryPlatform.service.RestaurantService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    @Cacheable(value = "restaurants", key = "#id")
    public Restaurant getById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Restaurant not found"));
    }

    @Override
    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    @Override
    @Transactional
    @Cacheable(value = "all_restaurants")
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    @Transactional
    @Cacheable(value = "restaurants_by_cuisine", key = "#cuisine")
    public List<Restaurant> getAllByCuisine(String cuisine) {
        return restaurantRepository.findAllByCuisine(cuisine);
    }

    @Override
    @Transactional
    public boolean existsRestaurant(UUID id) {
        return restaurantRepository.findById(id).isPresent();
    }

    @Override
    @Transactional
    public RestaurantClient getNameById(UUID id) {
        return new RestaurantClient(restaurantRepository.findById(id).get().getName());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", key = "#restaurant.id"),
            @CacheEvict(value = "all_restaurants", allEntries = true),
            @CacheEvict(value = "restaurants_by_cuisine", allEntries = true),
    })
    public Restaurant updateRestaurant(Restaurant restaurant) {
        Restaurant currentRestaurant = getById(restaurant.getId());
        currentRestaurant.setName(restaurant.getName());
        currentRestaurant.setAddress(restaurant.getAddress());
        currentRestaurant.setCuisine(restaurant.getCuisine());
        restaurantRepository.save(currentRestaurant);
        return currentRestaurant;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", key = "#id"),
            @CacheEvict(value = "all_restaurants", allEntries = true),
            @CacheEvict(value = "restaurants_by_cuisine", allEntries = true),
    })
    public void delete(UUID id) {
        restaurantRepository.deleteById(id);
    }
}
