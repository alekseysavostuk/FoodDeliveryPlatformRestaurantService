package v1.foodDeliveryPlatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
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
    public Dish setImagesByDishId(UUID dishId, List<String> images) {
        Dish dish = dishService.getById(dishId);
        List<String> updatedImages = new ArrayList<>(dish.getImages());
        updatedImages.addAll(images);
        dish.setImages(updatedImages);
        dishRepository.save(dish);
        return dish;
    }

    @Override
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
    @SneakyThrows
    public Dish removeAllImagesByDishId(UUID dishId) {
        Dish dish = dishService.getById(dishId);
        for (String image : dish.getImages()) {
            minioService.deleteFile(image);
        }
        dish.setImages(new ArrayList<>());
        return dishRepository.save(dish);
    }

    @Override
    public List<String> getAllByDishId(UUID dishId) {
        return dishService.getById(dishId).getImages();
    }


}
