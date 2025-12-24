package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import v1.foodDeliveryPlatform.exception.ResourceNotFoundException;
import v1.foodDeliveryPlatform.model.ModelImage;
import v1.foodDeliveryPlatform.model.Restaurant;
import v1.foodDeliveryPlatform.model.enums.Cuisine;
import v1.foodDeliveryPlatform.model.feign.RestaurantClient;
import v1.foodDeliveryPlatform.repository.RestaurantRepository;
import v1.foodDeliveryPlatform.service.MinioService;
import v1.foodDeliveryPlatform.service.RestaurantService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MinioService minioService;

    @Override
    @Transactional
    @Cacheable(value = "restaurants", key = "#id")
    public Restaurant getById(UUID id) {
        log.debug("Fetching restaurant by ID: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> {
            log.warn("Restaurant not found with ID: {}", id);
            return new ResourceNotFoundException("Restaurant not found");
        });
        log.debug("Successfully fetched restaurant: {} ({})", restaurant.getName(), restaurant.getId());
        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant) {
        isRestaurantCuisineExists(restaurant.getCuisine());
        log.info("Creating new restaurant: {}", restaurant.getName());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created successfully: {} ({})", savedRestaurant.getName(), savedRestaurant.getId());
        return savedRestaurant;
    }

    @Override
    @Transactional
    public List<Restaurant> getAllRestaurants() {
        log.debug("Fetching all restaurants");
        List<Restaurant> restaurants = restaurantRepository.findAll();
        log.debug("Found {} restaurants", restaurants.size());
        return restaurants;
    }

    @Override
    @Transactional
    public List<Restaurant> getAllByCuisine(String cuisine) {
        log.debug("Fetching restaurants by cuisine: {}", cuisine);
        List<Restaurant> restaurants = restaurantRepository.findAllByCuisine(cuisine);
        log.debug("Found {} restaurants with cuisine: {}", restaurants.size(), cuisine);
        return restaurants;
    }

    @Override
    @Transactional
    public boolean existsRestaurant(UUID id) {
        log.trace("Checking if restaurant exists: {}", id);
        boolean exists = restaurantRepository.findById(id).isPresent();
        log.trace("Restaurant existence check for {}: {}", id, exists);
        return exists;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", key = "#id"),
            @CacheEvict(value = "restaurants", allEntries = true)
    })
    public Restaurant uploadImage(final UUID id, final ModelImage image) {
        log.info("Uploading image for dish: {}", id);

        Restaurant restaurant = getById(id);
        log.debug("Restaurant found: {} with {} existing images", restaurant.getName(), restaurant.getImages().size());

        String fileName = minioService.upload(image);
        log.debug("Image uploaded to MinIO: {}", fileName);

        List<String> images = new ArrayList<>(restaurant.getImages());
        images.add(fileName);
        restaurant.setImages(images);

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Image uploaded successfully for restaurant: {} (total images: {})",
                updatedRestaurant.getName(), updatedRestaurant.getImages().size());

        return updatedRestaurant;
    }

    @Override
    @Transactional
    public RestaurantClient getNameById(UUID id) {
        log.debug("Fetching restaurant name by ID: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> {
            log.warn("Restaurant not found when fetching name for ID: {}", id);
            return new ResourceNotFoundException("Restaurant not found");
        });
        log.debug("Fetched restaurant name: {} for ID: {}", restaurant.getName(), id);
        return new RestaurantClient(restaurant.getName());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", key = "#restaurant.id"),
            @CacheEvict(value = "all_restaurants", allEntries = true),
            @CacheEvict(value = "restaurants_by_cuisine", allEntries = true),
    })
    public Restaurant updateRestaurant(Restaurant restaurant) {
        isRestaurantCuisineExists(restaurant.getCuisine());
        log.info("Updating restaurant with ID: {}", restaurant.getId());

        Restaurant currentRestaurant = getById(restaurant.getId());

        log.debug("Restaurant update details - Name: {} -> {}, Cuisine: {} -> {}, Address: {} -> {}",
                currentRestaurant.getName(), restaurant.getName(),
                currentRestaurant.getCuisine(), restaurant.getCuisine(),
                currentRestaurant.getAddress(), restaurant.getAddress());

        currentRestaurant.setName(restaurant.getName());
        currentRestaurant.setAddress(restaurant.getAddress());
        currentRestaurant.setCuisine(restaurant.getCuisine());

        Restaurant updatedRestaurant = restaurantRepository.save(currentRestaurant);
        log.info("Restaurant updated successfully: {} ({})", updatedRestaurant.getName(), updatedRestaurant.getId());

        return updatedRestaurant;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", key = "#id"),
            @CacheEvict(value = "all_restaurants", allEntries = true),
            @CacheEvict(value = "restaurants_by_cuisine", allEntries = true),
    })
    public void delete(UUID id) {
        log.info("Deleting restaurant with ID: {}", id);
        try {
            restaurantRepository.deleteImagesByRestaurantId(id);
            restaurantRepository.deleteById(id);
            log.info("Restaurant deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete restaurant with ID: {}", id, e);
            throw e;
        }
    }

    private void isRestaurantCuisineExists(String cuisine) {
        boolean isValid = Cuisine.isValidCuisine(cuisine);

        if (!isValid) {
            throw new IllegalArgumentException(
                    String.format("Invalid cuisine '%s'. Allowed values: %s",
                            cuisine,
                            String.join(", ", Cuisine.getNames()))
            );
        }
    }
}
