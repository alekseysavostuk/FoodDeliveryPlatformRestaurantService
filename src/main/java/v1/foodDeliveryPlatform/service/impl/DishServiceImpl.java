package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final RestaurantService restaurantService;
    private final MinioService minioService;

    @Override
    @Transactional
    @Cacheable(value = "dishes", key = "#id")
    public Dish getById(UUID id) {
        return dishRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Dish not found"));
    }

    @Override
    @Transactional
    public Dish createDish(Dish dish, UUID restaurantId) {
        dish.setRestaurant(restaurantService.getById(restaurantId));
        dishRepository.save(dish);
        return dish;
    }

    @Override
    @Transactional
    @Cacheable(value = "restaurant_dishes", key = "#restaurantId")
    public List<Dish> getAllByRestaurantId(UUID restaurantId) {
        return dishRepository.findAllByRestaurantId(restaurantId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#dish.id"),
            @CacheEvict(value = "restaurant_dishes", key = "#result.restaurant.id")
    })
    public Dish updateDish(Dish dish) {
        Dish currentDish = getById(dish.getId());
        currentDish.setName(dish.getName());
        currentDish.setPrice(dish.getPrice());
        currentDish.setDescription(dish.getDescription());
        dishRepository.save(currentDish);
        return currentDish;
    }

    @SneakyThrows
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#id"),
            @CacheEvict(value = "restaurant_dishes", allEntries = true)
    })
    public void delete(UUID id) {
        Dish dish = getById(id);
        for (String image : dish.getImages()) {
            minioService.deleteFile(image);
        }
        dishRepository.deleteImagesByDishId(id);
        dishRepository.deleteDirectlyById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#id"),
            @CacheEvict(value = "restaurant_dishes", key = "#result.restaurant.id")
    })
    public Dish uploadImage(
            final UUID id,
            final DishImage image
    ) {
        Dish dish = getById(id);
        String fileName = minioService.upload(image);
        List<String> images = new ArrayList<>(dish.getImages());
        images.add(fileName);
        dish.setImages(images);
        return dishRepository.save(dish);
    }

    @Override
    public boolean existsDish(UUID restaurantId, UUID dishId) {
        return dishRepository.findById(dishId).isPresent()
                && getById(dishId).getRestaurant().getId().equals(restaurantId);
    }

    @Override
    public DishClient getNameById(UUID id) {
        return new DishClient(dishRepository.findById(id).get().getName());
    }
}
