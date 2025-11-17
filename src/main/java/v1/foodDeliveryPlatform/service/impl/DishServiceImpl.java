package v1.foodDeliveryPlatform.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import v1.foodDeliveryPlatform.exception.ModelExistsException;
import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.model.DishImage;
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
    public Dish getById(UUID id) {
        return dishRepository.findById(id).orElseThrow(() ->
                new ModelExistsException("Dish not found"));
    }

    @Override
    public Dish createDish(Dish dish, UUID restaurantId) {
        dish.setRestaurant(restaurantService.getById(restaurantId));
        dishRepository.save(dish);
        return dish;
    }

    @Override
    @Transactional
    public List<Dish> getAllByRestaurantId(UUID restaurantId) {
        return dishRepository.findAllByRestaurantId(restaurantId);
    }

    @Override
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
}
