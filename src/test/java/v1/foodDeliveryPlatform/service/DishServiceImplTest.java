package v1.foodDeliveryPlatform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import v1.foodDeliveryPlatform.exception.ResourceNotFoundException;
import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.model.ModelImage;
import v1.foodDeliveryPlatform.model.Restaurant;
import v1.foodDeliveryPlatform.model.feign.DishClient;
import v1.foodDeliveryPlatform.repository.DishRepository;
import v1.foodDeliveryPlatform.service.impl.DishServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private DishServiceImpl dishService;

    private final UUID dishId = UUID.randomUUID();
    private final UUID restaurantId = UUID.randomUUID();
    private final String dishName = "Test Dish";

    @Test
    void getById_Success() {
        Dish dish = createTestDish();
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        Dish result = dishService.getById(dishId);

        assertNotNull(result);
        assertEquals(dishId, result.getId());
        assertEquals(dishName, result.getName());
        verify(dishRepository).findById(dishId);
    }

    @Test
    void getById_NotFound() {
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> dishService.getById(dishId));

        assertEquals("Dish not found", exception.getMessage());
        verify(dishRepository).findById(dishId);
    }

    @Test
    void createDish_Success() {
        Dish dish = createTestDish();
        dish.setId(null);
        Restaurant restaurant = createTestRestaurant();
        Dish savedDish = createTestDish();

        when(restaurantService.getById(restaurantId)).thenReturn(restaurant);
        when(dishRepository.save(dish)).thenReturn(savedDish);

        Dish result = dishService.createDish(dish, restaurantId);

        assertNotNull(result);
        assertEquals(dishId, result.getId());
        assertEquals(restaurant, dish.getRestaurant());
        verify(restaurantService).getById(restaurantId);
        verify(dishRepository).save(dish);
    }

    @Test
    void getAllByRestaurantId_Success() {
        Dish dish1 = createTestDish();
        Dish dish2 = createTestDish();
        dish2.setId(UUID.randomUUID());
        dish2.setName("Another Dish");

        when(dishRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(dish1, dish2));

        List<Dish> result = dishService.getAllByRestaurantId(restaurantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dishRepository).findAllByRestaurantId(restaurantId);
    }

    @Test
    void updateDish_Success() {
        Dish existingDish = createTestDish();
        Dish updateData = createTestDish();
        updateData.setName("Updated Dish");
        updateData.setPrice(new BigDecimal("2000.00"));
        updateData.setDescription("Updated description");

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));
        when(dishRepository.save(existingDish)).thenReturn(existingDish);

        Dish result = dishService.updateDish(updateData);

        assertNotNull(result);
        assertEquals("Updated Dish", existingDish.getName());
        assertEquals(new BigDecimal("2000.00"), existingDish.getPrice());
        assertEquals("Updated description", existingDish.getDescription());
        verify(dishRepository).findById(dishId);
        verify(dishRepository).save(existingDish);
    }

    @Test
    void updateDish_NotFound() {
        Dish updateData = createTestDish();
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> dishService.updateDish(updateData));

        assertEquals("Dish not found", exception.getMessage());
        verify(dishRepository).findById(dishId);
        verify(dishRepository, never()).save(any());
    }

    @Test
    void delete_Success() throws Exception {
        Dish dish = createTestDish();
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        doNothing().when(minioService).deleteFile(anyString());
        doNothing().when(dishRepository).deleteImagesByDishId(dishId);
        doNothing().when(dishRepository).deleteDirectlyById(dishId);

        assertDoesNotThrow(() -> dishService.delete(dishId));

        verify(dishRepository).findById(dishId);
        verify(minioService, times(2)).deleteFile(anyString());
        verify(dishRepository).deleteImagesByDishId(dishId);
        verify(dishRepository).deleteDirectlyById(dishId);
    }

    @Test
    void delete_NoImages() throws Exception {
        Dish dish = createTestDish();
        dish.setImages(List.of());
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        doNothing().when(dishRepository).deleteImagesByDishId(dishId);
        doNothing().when(dishRepository).deleteDirectlyById(dishId);

        assertDoesNotThrow(() -> dishService.delete(dishId));

        verify(dishRepository).findById(dishId);
        verify(minioService, never()).deleteFile(anyString());
        verify(dishRepository).deleteImagesByDishId(dishId);
        verify(dishRepository).deleteDirectlyById(dishId);
    }

    @Test
    void delete_MinioException() throws Exception {
        Dish dish = createTestDish();
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        doThrow(new RuntimeException("MinIO error")).when(minioService).deleteFile(anyString());

        assertThrows(RuntimeException.class, () -> dishService.delete(dishId));

        verify(dishRepository).findById(dishId);
        verify(minioService).deleteFile(anyString());
        verify(dishRepository, never()).deleteImagesByDishId(any());
        verify(dishRepository, never()).deleteDirectlyById(any());
    }

    @Test
    void uploadImage_Success() {
        Dish dish = createTestDish();
        ModelImage modelImage = new ModelImage();
        String fileName = "uploaded-image.jpg";

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        when(minioService.upload(modelImage)).thenReturn(fileName);
        when(dishRepository.save(dish)).thenReturn(dish);

        Dish result = dishService.uploadImage(dishId, modelImage);

        assertNotNull(result);
        assertEquals(3, dish.getImages().size());
        assertTrue(dish.getImages().contains(fileName));
        verify(dishRepository).findById(dishId);
        verify(minioService).upload(modelImage);
        verify(dishRepository).save(dish);
    }

    @Test
    void uploadImage_DishNotFound() {
        ModelImage modelImage = new ModelImage();
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> dishService.uploadImage(dishId, modelImage));

        assertEquals("Dish not found", exception.getMessage());
        verify(dishRepository).findById(dishId);
        verify(minioService, never()).upload(any());
        verify(dishRepository, never()).save(any());
    }

    @Test
    void existsDish_True() {
        Dish dish = createTestDish();
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        boolean result = dishService.existsDish(restaurantId, dishId);

        assertTrue(result);
        verify(dishRepository, times(2)).findById(dishId);
    }

    @Test
    void existsDish_False_DishNotFound() {
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        boolean result = dishService.existsDish(restaurantId, dishId);

        assertFalse(result);
        verify(dishRepository).findById(dishId);
    }

    @Test
    void existsDish_False_WrongRestaurant() {
        Dish dish = createTestDish();
        dish.getRestaurant().setId(UUID.randomUUID());
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        boolean result = dishService.existsDish(restaurantId, dishId);

        assertFalse(result);
        verify(dishRepository, times(2)).findById(dishId);
    }

    @Test
    void getNameById_Success() {
        Dish dish = createTestDish();
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        DishClient result = dishService.getNameById(dishId);

        assertNotNull(result);
        assertEquals(dishName, result.getDishName());
        verify(dishRepository).findById(dishId);
    }

    @Test
    void getNameById_NotFound() {
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> dishService.getNameById(dishId));

        assertEquals("Dish not found", exception.getMessage());
        verify(dishRepository).findById(dishId);
    }

    @Test
    void getAllByRestaurantId_Empty() {
        when(dishRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of());

        List<Dish> result = dishService.getAllByRestaurantId(restaurantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dishRepository).findAllByRestaurantId(restaurantId);
    }

    private Dish createTestDish() {
        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setName(dishName);
        dish.setPrice(new BigDecimal("1500.00"));
        dish.setDescription("Test description");
        dish.setRestaurant(createTestRestaurant());
        dish.setImages(List.of("image1.jpg", "image2.jpg"));
        return dish;
    }

    private Restaurant createTestRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");
        return restaurant;
    }
}
