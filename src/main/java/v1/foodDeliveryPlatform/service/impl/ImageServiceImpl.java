package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.repository.DishRepository;
import v1.foodDeliveryPlatform.service.DishService;
import v1.foodDeliveryPlatform.service.ImageService;
import v1.foodDeliveryPlatform.service.MinioService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final DishService dishService;
    private final DishRepository dishRepository;
    private final MinioService minioService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#dishId"),
            @CacheEvict(value = "restaurant_dishes", allEntries = true)
    })
    public Dish removeImageByDishId(UUID dishId, String image) throws Exception {
        Dish dish = dishService.getById(dishId);

        List<String> updatedImages = new ArrayList<>(dish.getImages());
        if (updatedImages.remove(image)) {

            minioService.deleteFile(image);
            dish.setImages(updatedImages);
            dishRepository.save(dish);
        }
        return dish;
    }

    @Override
    @Transactional
    @SneakyThrows
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#dishId"),
            @CacheEvict(value = "restaurant_dishes", allEntries = true)
    })
    public Dish removeAllImagesByDishId(UUID dishId) {
        Dish dish = dishService.getById(dishId);
        for (String image : dish.getImages()) {
            minioService.deleteFile(image);
        }
        dish.setImages(new ArrayList<>());
        return dishRepository.save(dish);
    }

    @Override
    @Cacheable(value = "dish_images", key = "#dishId")
    public List<String> getAllByDishId(UUID dishId) {
        return dishService.getById(dishId).getImages();
    }
}
