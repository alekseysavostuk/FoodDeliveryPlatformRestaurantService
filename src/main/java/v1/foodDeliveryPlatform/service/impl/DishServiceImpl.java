package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import v1.foodDeliveryPlatform.exception.ResourceNotFoundException;
import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.model.DishImage;
import v1.foodDeliveryPlatform.model.feign.DishClient;
import v1.foodDeliveryPlatform.repository.DishRepository;
import v1.foodDeliveryPlatform.service.DishService;
import v1.foodDeliveryPlatform.service.MinioService;
import v1.foodDeliveryPlatform.service.RestaurantService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final RestaurantService restaurantService;
    private final MinioService minioService;

    @Override
    @Transactional
    @Cacheable(value = "dishes", key = "#id")
    public Dish getById(UUID id) {
        log.debug("Fetching dish by ID: {}", id);
        Dish dish = dishRepository.findById(id).orElseThrow(() -> {
            log.warn("Dish not found with ID: {}", id);
            return new ResourceNotFoundException("Dish not found");
        });
        log.debug("Successfully fetched dish: {} ({})", dish.getName(), dish.getId());
        return dish;
    }

    @Override
    @Transactional
    public Dish createDish(Dish dish, UUID restaurantId) {
        log.info("Creating new dish: {} for restaurant: {}", dish.getName(), restaurantId);

        dish.setRestaurant(restaurantService.getById(restaurantId));
        Dish savedDish = dishRepository.save(dish);

        log.info("Dish created successfully: {} ({}) for restaurant: {}",
                savedDish.getName(), savedDish.getId(), restaurantId);
        return savedDish;
    }

    @Override
    @Transactional
    @Cacheable(value = "restaurant_dishes", key = "#restaurantId")
    public List<Dish> getAllByRestaurantId(UUID restaurantId) {
        log.debug("Fetching all dishes for restaurant: {}", restaurantId);
        List<Dish> dishes = dishRepository.findAllByRestaurantId(restaurantId);
        log.debug("Found {} dishes for restaurant: {}", dishes.size(), restaurantId);
        return dishes;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#dish.id"),
            @CacheEvict(value = "restaurant_dishes", key = "#result.restaurant.id")
    })
    public Dish updateDish(Dish dish) {
        log.info("Updating dish with ID: {}", dish.getId());

        Dish currentDish = getById(dish.getId());

        log.debug("Dish update details - Name: {} -> {}, Price: {} -> {}, Description length: {} -> {}",
                currentDish.getName(), dish.getName(),
                currentDish.getPrice(), dish.getPrice(),
                currentDish.getDescription().length(), dish.getDescription().length());

        currentDish.setName(dish.getName());
        currentDish.setPrice(dish.getPrice());
        currentDish.setDescription(dish.getDescription());

        Dish updatedDish = dishRepository.save(currentDish);
        log.info("Dish updated successfully: {} ({})", updatedDish.getName(), updatedDish.getId());

        return updatedDish;
    }

    @SneakyThrows
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#id"),
            @CacheEvict(value = "restaurant_dishes", allEntries = true)
    })
    public void delete(UUID id) {
        log.info("Deleting dish with ID: {}", id);

        Dish dish = getById(id);
        log.debug("Deleting {} images for dish: {}", dish.getImages().size(), dish.getName());

        for (String image : dish.getImages()) {
            log.trace("Deleting image from MinIO: {}", image);
            minioService.deleteFile(image);
        }

        dishRepository.deleteImagesByDishId(id);
        dishRepository.deleteDirectlyById(id);

        log.info("Dish deleted successfully: {} ({})", dish.getName(), id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#id"),
            @CacheEvict(value = "restaurant_dishes", key = "#result.restaurant.id")
    })
    public Dish uploadImage(final UUID id, final DishImage image) {
        log.info("Uploading image for dish: {}", id);

        Dish dish = getById(id);
        log.debug("Dish found: {} with {} existing images", dish.getName(), dish.getImages().size());

        String fileName = minioService.upload(image);
        log.debug("Image uploaded to MinIO: {}", fileName);

        List<String> images = new ArrayList<>(dish.getImages());
        images.add(fileName);
        dish.setImages(images);

        Dish updatedDish = dishRepository.save(dish);
        log.info("Image uploaded successfully for dish: {} (total images: {})",
                updatedDish.getName(), updatedDish.getImages().size());

        return updatedDish;
    }

    @Override
    public boolean existsDish(UUID restaurantId, UUID dishId) {
        log.trace("Checking if dish exists - DishId: {}, RestaurantId: {}", dishId, restaurantId);

        boolean exists = dishRepository.findById(dishId).isPresent()
                && getById(dishId).getRestaurant().getId().equals(restaurantId);

        log.trace("Dish existence check result: {} for DishId: {}, RestaurantId: {}", exists, dishId, restaurantId);
        return exists;
    }

    @Override
    public DishClient getNameById(UUID id) {
        log.debug("Fetching dish name by ID: {}", id);

        Dish dish = dishRepository.findById(id).orElseThrow(() -> {
            log.warn("Dish not found when fetching name for ID: {}", id);
            return new ResourceNotFoundException("Dish not found");
        });

        log.debug("Fetched dish name: {} for ID: {}", dish.getName(), id);
        return new DishClient(dish.getName());
    }
}
