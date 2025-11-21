package v1.foodDeliveryPlatform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.repository.DishRepository;
import v1.foodDeliveryPlatform.service.impl.ImageServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private DishService dishService;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ImageServiceImpl imageService;

    private final UUID dishId = UUID.randomUUID();
    private final String imageName = "test-image.jpg";

    @Test
    void removeImageByDishId_Success() throws Exception {
        Dish dish = createTestDish();
        List<String> originalImages = new ArrayList<>(dish.getImages());

        when(dishService.getById(dishId)).thenReturn(dish);
        doNothing().when(minioService).deleteFile(imageName);
        when(dishRepository.save(dish)).thenReturn(dish);

        Dish result = imageService.removeImageByDishId(dishId, imageName);

        assertNotNull(result);
        assertEquals(1, result.getImages().size());
        assertFalse(result.getImages().contains(imageName));
        verify(dishService).getById(dishId);
        verify(minioService).deleteFile(imageName);
        verify(dishRepository).save(dish);
    }

    @Test
    void removeImageByDishId_ImageNotFound() throws Exception {
        Dish dish = createTestDish();
        String nonExistentImage = "non-existent.jpg";

        when(dishService.getById(dishId)).thenReturn(dish);

        Dish result = imageService.removeImageByDishId(dishId, nonExistentImage);

        assertNotNull(result);
        assertEquals(2, result.getImages().size());
        verify(dishService).getById(dishId);
        verify(minioService, never()).deleteFile(any());
        verify(dishRepository, never()).save(any());
    }

    @Test
    void removeImageByDishId_MinioException() throws Exception {
        Dish dish = createTestDish();

        when(dishService.getById(dishId)).thenReturn(dish);
        doThrow(new RuntimeException("MinIO error")).when(minioService).deleteFile(imageName);

        Exception exception = assertThrows(Exception.class,
                () -> imageService.removeImageByDishId(dishId, imageName));

        assertEquals("MinIO error", exception.getMessage());
        verify(dishService).getById(dishId);
        verify(minioService).deleteFile(imageName);
        verify(dishRepository, never()).save(any());
    }

    @Test
    void removeAllImagesByDishId_Success() throws Exception {
        Dish dish = createTestDish();
        Dish savedDish = createTestDish();
        savedDish.setImages(new ArrayList<>());

        when(dishService.getById(dishId)).thenReturn(dish);
        doNothing().when(minioService).deleteFile(anyString());
        when(dishRepository.save(dish)).thenReturn(savedDish);

        Dish result = imageService.removeAllImagesByDishId(dishId);

        assertNotNull(result);
        assertTrue(result.getImages().isEmpty());
        verify(dishService).getById(dishId);
        verify(minioService, times(2)).deleteFile(anyString());
        verify(dishRepository).save(dish);
    }

    @Test
    void removeAllImagesByDishId_NoImages() throws Exception {
        Dish dish = createTestDish();
        dish.setImages(new ArrayList<>());
        Dish savedDish = createTestDish();
        savedDish.setImages(new ArrayList<>());

        when(dishService.getById(dishId)).thenReturn(dish);
        when(dishRepository.save(dish)).thenReturn(savedDish);

        Dish result = imageService.removeAllImagesByDishId(dishId);

        assertNotNull(result);
        assertTrue(result.getImages().isEmpty());
        verify(dishService).getById(dishId);
        verify(minioService, never()).deleteFile(anyString());
        verify(dishRepository).save(dish);
    }

    @Test
    void removeAllImagesByDishId_MinioException() throws Exception {
        Dish dish = createTestDish();

        when(dishService.getById(dishId)).thenReturn(dish);
        doThrow(new RuntimeException("MinIO error")).when(minioService).deleteFile(anyString());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> imageService.removeAllImagesByDishId(dishId));

        assertEquals("MinIO error", exception.getMessage());
        verify(dishService).getById(dishId);
        verify(minioService).deleteFile(anyString());
        verify(dishRepository, never()).save(any());
    }

    @Test
    void getAllByDishId_Success() {
        Dish dish = createTestDish();
        List<String> expectedImages = dish.getImages();

        when(dishService.getById(dishId)).thenReturn(dish);

        List<String> result = imageService.getAllByDishId(dishId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedImages, result);
        verify(dishService).getById(dishId);
    }

    @Test
    void getAllByDishId_EmptyImages() {
        Dish dish = createTestDish();
        dish.setImages(new ArrayList<>());

        when(dishService.getById(dishId)).thenReturn(dish);

        List<String> result = imageService.getAllByDishId(dishId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dishService).getById(dishId);
    }

    @Test
    void removeImageByDishId_SingleImage() throws Exception {
        Dish dish = createTestDish();
        dish.setImages(List.of(imageName));

        when(dishService.getById(dishId)).thenReturn(dish);
        doNothing().when(minioService).deleteFile(imageName);
        when(dishRepository.save(dish)).thenReturn(dish);

        Dish result = imageService.removeImageByDishId(dishId, imageName);

        assertNotNull(result);
        assertTrue(result.getImages().isEmpty());
        verify(dishService).getById(dishId);
        verify(minioService).deleteFile(imageName);
        verify(dishRepository).save(dish);
    }

    private Dish createTestDish() {
        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setName("Test Dish");
        dish.setImages(new ArrayList<>(List.of("image1.jpg", imageName)));
        return dish;
    }
}
