package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Removing image from dish - DishId: {}, Image: {}", dishId, image);

        Dish dish = dishService.getById(dishId);
        log.debug("Found dish: {} with {} images", dish.getName(), dish.getImages().size());

        List<String> updatedImages = new ArrayList<>(dish.getImages());
        if (updatedImages.remove(image)) {
            log.debug("Image found in dish, removing from MinIO and updating dish");
            minioService.deleteFile(image);
            dish.setImages(updatedImages);
            Dish savedDish = dishRepository.save(dish);
            log.info("Image removed successfully from dish: {} (remaining images: {})",
                    dishId, updatedImages.size());
            return savedDish;
        } else {
            log.warn("Image not found in dish images - DishId: {}, Image: {}", dishId, image);
            return dish;
        }
    }

    @Override
    @Transactional
    @SneakyThrows
    @Caching(evict = {
            @CacheEvict(value = "dishes", key = "#dishId"),
            @CacheEvict(value = "restaurant_dishes", allEntries = true)
    })
    public Dish removeAllImagesByDishId(UUID dishId) {
        log.info("Removing all images from dish: {}", dishId);

        Dish dish = dishService.getById(dishId);
        log.debug("Found dish: {} with {} images to remove", dish.getName(), dish.getImages().size());

        int imageCount = dish.getImages().size();
        for (String image : dish.getImages()) {
            log.debug("Deleting image from MinIO: {}", image);
            minioService.deleteFile(image);
        }

        dish.setImages(new ArrayList<>());
        Dish savedDish = dishRepository.save(dish);
        log.info("All {} images removed successfully from dish: {}", imageCount, dishId);

        return savedDish;
    }

    @Override
    public List<String> getAllByDishId(UUID dishId) {
        log.debug("Fetching all images for dish: {}", dishId);

        Dish dish = dishService.getById(dishId);
        List<String> images = dish.getImages();

        log.debug("Found {} images for dish: {}", images.size(), dishId);
        return images;
    }
}
