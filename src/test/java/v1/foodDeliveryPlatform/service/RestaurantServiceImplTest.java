package v1.foodDeliveryPlatform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import v1.foodDeliveryPlatform.exception.ResourceNotFoundException;
import v1.foodDeliveryPlatform.model.Restaurant;
import v1.foodDeliveryPlatform.model.feign.RestaurantClient;
import v1.foodDeliveryPlatform.repository.RestaurantRepository;
import v1.foodDeliveryPlatform.service.impl.RestaurantServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private final UUID restaurantId = UUID.randomUUID();
    private final String restaurantName = "Test Restaurant";

    @Test
    void getById_Success() {
        Restaurant restaurant = createTestRestaurant();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getById(restaurantId);

        assertNotNull(result);
        assertEquals(restaurantId, result.getId());
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void getById_NotFound() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> restaurantService.getById(restaurantId));

        assertEquals("Restaurant not found", exception.getMessage());
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void createRestaurant_Success() {
        Restaurant newRestaurant = createTestRestaurant();
        newRestaurant.setId(null);
        when(restaurantRepository.save(newRestaurant)).thenReturn(createTestRestaurant());

        Restaurant result = restaurantService.createRestaurant(newRestaurant);

        assertNotNull(result);
        assertEquals(restaurantName, result.getName());
        verify(restaurantRepository).save(newRestaurant);
    }

    @Test
    void getAllRestaurants_Success() {
        Restaurant restaurant1 = createTestRestaurant();
        Restaurant restaurant2 = createTestRestaurant();
        restaurant2.setId(UUID.randomUUID());
        restaurant2.setName("Another Restaurant");

        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant1, restaurant2));

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restaurantRepository).findAll();
    }

    @Test
    void getAllByCuisine_Success() {
        String cuisine = "Italian";
        Restaurant restaurant = createTestRestaurant();
        when(restaurantRepository.findAllByCuisine(cuisine)).thenReturn(List.of(restaurant));

        List<Restaurant> result = restaurantService.getAllByCuisine(cuisine);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cuisine, result.getFirst().getCuisine());
        verify(restaurantRepository).findAllByCuisine(cuisine);
    }

    @Test
    void existsRestaurant_True() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(createTestRestaurant()));

        boolean result = restaurantService.existsRestaurant(restaurantId);

        assertTrue(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void existsRestaurant_False() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        boolean result = restaurantService.existsRestaurant(restaurantId);

        assertFalse(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void getNameById_Success() {
        Restaurant restaurant = createTestRestaurant();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        RestaurantClient result = restaurantService.getNameById(restaurantId);

        assertNotNull(result);
        assertEquals(restaurantName, result.getRestaurantName());
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void getNameById_NotFound() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> restaurantService.getNameById(restaurantId));

        assertEquals("Restaurant not found", exception.getMessage());
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void updateRestaurant_Success() {
        Restaurant existingRestaurant = createTestRestaurant();
        Restaurant updateData = createTestRestaurant();
        updateData.setName("Updated Restaurant");
        updateData.setAddress("Updated Address");
        updateData.setCuisine("Mexican");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
        when(restaurantRepository.save(existingRestaurant)).thenReturn(existingRestaurant);

        Restaurant result = restaurantService.updateRestaurant(updateData);

        assertNotNull(result);
        assertEquals("Updated Restaurant", existingRestaurant.getName());
        assertEquals("Updated Address", existingRestaurant.getAddress());
        assertEquals("Mexican", existingRestaurant.getCuisine());
        verify(restaurantRepository).findById(restaurantId);
        verify(restaurantRepository).save(existingRestaurant);
    }

    @Test
    void updateRestaurant_NotFound() {
        Restaurant updateData = createTestRestaurant();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> restaurantService.updateRestaurant(updateData));

        assertEquals("Restaurant not found", exception.getMessage());
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void delete_Success() {
        doNothing().when(restaurantRepository).deleteById(restaurantId);

        assertDoesNotThrow(() -> restaurantService.delete(restaurantId));

        verify(restaurantRepository).deleteById(restaurantId);
    }

    @Test
    void delete_Exception() {
        doThrow(new RuntimeException("DB error")).when(restaurantRepository).deleteById(restaurantId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> restaurantService.delete(restaurantId));

        assertEquals("DB error", exception.getMessage());
        verify(restaurantRepository).deleteById(restaurantId);
    }

    @Test
    void getAllByCuisine_Empty() {
        String cuisine = "Japanese";
        when(restaurantRepository.findAllByCuisine(cuisine)).thenReturn(List.of());

        List<Restaurant> result = restaurantService.getAllByCuisine(cuisine);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restaurantRepository).findAllByCuisine(cuisine);
    }

    @Test
    void getAllRestaurants_Empty() {
        when(restaurantRepository.findAll()).thenReturn(List.of());

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restaurantRepository).findAll();
    }

    private Restaurant createTestRestaurant() {
        return Restaurant.builder()
                .id(restaurantId)
                .name(restaurantName)
                .address("Test Address")
                .cuisine("Italian")
                .build();
    }
}